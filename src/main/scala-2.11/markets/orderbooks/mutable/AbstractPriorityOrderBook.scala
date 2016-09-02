/*
Copyright 2016 ScalABM

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
package markets.orderbooks.mutable

import markets.orderbooks.AbstractOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable


/** Abstract class defining the interface for a `PriorityOrderBook`.
  *
  * @param tradable all `Orders` contained in a `PriorityOrderBook` should be for the same `Tradable`.
  * @tparam A the type of `Order` stored in a `PriorityOrderBook`.
  */
abstract class AbstractPriorityOrderBook[A <: Order](tradable: Tradable)
  extends AbstractOrderBook[A](tradable) {

  /** Return the head `Order` of the `PriorityOrderBook`.
    *
    * @return `None` if the `PriorityOrderBook` is empty; `Some(order)` otherwise.
    * @note the head `Order` of the `PriorityOrderBook` is the head `Order` of the underlying `prioritisedOrders`.
    */
  override def headOption: Option[A] = prioritisedOrders.headOption

  /* Underlying prioritised collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] def prioritisedOrders: mutable.PriorityQueue[A]

}

