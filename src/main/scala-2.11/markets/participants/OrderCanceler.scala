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
package markets.participants

import markets.participants.strategies.CancellationStrategy
import markets.{Cancel, Canceled}
import markets.orders.Order


/** Mixin Trait providing behavior necessary to cancel outstanding orders. */
trait OrderCanceler extends MarketParticipant {

  def cancellationStrategy: CancellationStrategy

  override def receive: Receive = {
    case Canceled(order, _, _) =>
      outstandingOrders -= order
    case SubmitOrderCancellation =>
      val canceledOrder = cancellationStrategy.cancelOneOf(outstandingOrders)
      canceledOrder match {
        case Some(order) =>
          val orderCancellation = generateOrderCancellation(order)
          submit(orderCancellation)
        case None =>  // no outstanding orders to cancel!
      }
    case message => super.receive(message)
  }

  protected object SubmitOrderCancellation

  private[this] def submit(orderCancellation: Cancel): Unit = {
    val market = markets(orderCancellation.order.tradable)
    market tell(orderCancellation, self)
  }

  private[this] def generateOrderCancellation(order: Order): Cancel = {
    Cancel(order, timestamp(), uuid())
  }

}
