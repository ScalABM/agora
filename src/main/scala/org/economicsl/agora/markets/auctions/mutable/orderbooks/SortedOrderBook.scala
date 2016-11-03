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
package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Order

import scala.collection.mutable


/** Class for modeling an `OrderBook` where the underlying collection of orders is sorted.
  *
  * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
  * @param ordering an `Ordering` used to compare `Order` instances.
  * @tparam O the type of `Order` stored in the `SortedOrderBook`.
  */
class SortedOrderBook[O <: Order](tradable: Tradable)(implicit ordering: Ordering[O]) extends OrderBook[O](tradable)
  with SortedOrders[O] {

  /* Underlying sorted collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] val sortedOrders = mutable.TreeSet.empty[O](ordering)

}


/** Companion object for `SortedOrderBook`.
  *
  * Used as a factory for creating `SortedOrderBook` instances.
  */
object SortedOrderBook {

  /** Create a `SortedOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
    * @param ordering an `Ordering` used to compare `Order` instances.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable)(implicit ordering: Ordering[O]): SortedOrderBook[O] =  {
    new SortedOrderBook[O](tradable)(ordering)
  }

}