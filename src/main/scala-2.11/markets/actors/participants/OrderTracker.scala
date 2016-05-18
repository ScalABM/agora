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

import markets.actors.{Accepted, Filled}
import markets.orders.Order

import scala.collection.immutable


/** Mixin trait providing behavior necessary to keep track of outstanding orders. */
trait OrderTracker {
  this: OrderIssuer =>

  var outstandingOrders: immutable.Set[Order]

  def orderTrackerBehavior: Receive = {
    case Accepted(order) =>
      outstandingOrders += order
    case Filled(order, residual) =>
      outstandingOrders -= order
      residual match {
        case Some(residualOrder) =>
          outstandingOrders += residualOrder
        case None =>  // do nothing!
      }
  }

}
