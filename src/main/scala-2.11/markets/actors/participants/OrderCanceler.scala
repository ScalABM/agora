/*
Copyright 2016 David R. Pugh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package markets.actors.participants

import markets.actors.participants.strategies.CancellationStrategy
import markets.actors.{Accepted, Cancel, Canceled, Filled}
import markets.orders.Order

import scala.collection.mutable


/** Mixin Trait providing behavior necessary to cancel outstanding orders. */
trait OrderCanceler {
  this: MarketParticipant with OrderIssuer =>

  def cancellationStrategy: CancellationStrategy

  def outstandingOrders: mutable.Set[Order]

  def orderCancelerBehavior: Receive = {
    case Canceled(order, _, _) =>
      outstandingOrders -= order
    case SubmitOrderCancellation =>
      val canceledOrder = cancellationStrategy.cancelOneOf(outstandingOrders)
      canceledOrder match {
        case Some(order) =>
          val orderCancellation = generateOrderCancellation(order)
          issue(orderCancellation)
        case None =>  // no outstanding orders to cancel!
      }

    case Accepted(order, _, _) =>
      outstandingOrders += order
    case Filled(order, residual, _, _) =>
      outstandingOrders -= order
      residual match {
        case Some(residualOrder) =>
          outstandingOrders += residualOrder
        case None =>  // do nothing!
      }
  }

  private[this] def issue(orderCancellation: Cancel): Unit = {
    val market = markets(orderCancellation.order.tradable)
    market tell(orderCancellation, self)
  }

  private[this] def generateOrderCancellation(order: Order): Cancel = {
    Cancel(order, timestamp(), uuid())
  }

}
