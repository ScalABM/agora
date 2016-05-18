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

import markets.actors.participants.strategies.OrderCancellationStrategy
import markets.actors.{Cancel, Canceled}


/** Mixin Trait providing behavior necessary to cancel outstanding orders. */
trait OrderCanceler {
  this: OrderIssuer with OrderTracker =>

  def orderCancellationStrategy: OrderCancellationStrategy

  def orderCancelerBehavior: Receive = {
    case IssueOrderCancellation =>
      val canceledOrder = orderCancellationStrategy.cancelOneOf(outstandingOrders)
      canceledOrder match {
        case Some(order) =>
          val market = markets(order.tradable)
          val cancellation = Cancel(order)
          market tell(cancellation, self)
        case None =>  // no outstanding orders to cancel!
      }
    case Canceled(order) =>
      outstandingOrders -= order
  }

}
