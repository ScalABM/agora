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
package markets.orderbooks

import markets.orders.Order
import markets.tradables.Tradable


/** Abstract class defining the interface for a `SortedOrderBook`.
  *
  * @param tradable all `Orders` contained in a `SortedOrderBook` should be for the same `Tradable`.
  * @tparam A the type of `Order` stored in a `SortedOrderBook`.
  */
abstract class AbstractSortedOrderBook[A <: Order](tradable: Tradable)
  extends AbstractOrderBook[A](tradable) {

  /** Return the head `Order` of the `SortedOrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    * @note the head `Order` of the `SortedOrderBook` is the head `Order` of the underlying `sortedOrders`.
    */
  override def headOption: Option[A] = sortedOrders.headOption

  /** Find the first `Order` in the `SortedOrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `SortedOrderBook` satisfies the predicate; `Some(order)` otherwise.
    * @note `find` iterates over the `SortedOrderBook` in ascending order starting from the `head` `Order`.
    */
  override def find(p: (A) => Boolean): Option[A] = {
    sortedOrders.find(p)
  }

  /* Underlying sorted collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] def sortedOrders: collection.SortedSet[A]

}

