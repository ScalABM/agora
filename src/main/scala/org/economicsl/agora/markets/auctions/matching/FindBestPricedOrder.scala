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
package org.economicsl.agora.markets.auctions.matching

import org.economicsl.agora.markets.auctions.mutable.orderbooks.{OrderBook, SortedOrders}
import org.economicsl.agora.markets.tradables.orders.{NonPriceCriteria, Order, PriceCriteria}


/** Class defining a function that matches an incoming order with the "best priced" order that satisfies the incoming
  * order's price (and possibly non-price) criteria.
  *
  * @tparam O1 the type of `Order` instances that should be matched by the function.
  * @tparam O2 the type of `Order` instances that are potential matches and are stored in the `OrderBook`.
  * @todo the type of `O2` should indicate that it must be priced.
  */
class FindBestPricedOrder[-O1 <: Order with PriceCriteria[O2] with NonPriceCriteria[O2], O2 <: Order]
  extends ((O1, OrderBook[O2] with SortedOrders[O2]) => Option[O2]) {

  /** Matches a given `Order` with the first acceptable `Order` found in some `SortedOrderBook`.
    *
    * @param order an `Order` in search of a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if no acceptable `Order` is found in the `orderBook`; `Some(matchingOrder)` otherwise.
    * @note Worst case performance of this matching function is `O(n)` where `n` is the number of `Order` instances
    *       contained in the `orderBook` (however for an `order` which has `nonPriceCriteria=None`, the worst case
    *       performance is `O(1)`).  Depending on the type of `orderBook`, the result of this `MatchingFunction`
    *       may be non-deterministic.
    * @todo this function can be used with any kind of `SortedOrderBook` or `PrioritisedOrderBook`...
    */
  def apply(order: O1, orderBook: OrderBook[O2] with SortedOrders[O2]): Option[O2] = {
    order.nonPriceCriteria match {
      case Some(nonPriceCriteria) => orderBook.find(order.isAcceptable)
      case None => orderBook.headOption.find(order.isAcceptable)
    }
  }

}

object FindBestPricedOrder {

  def apply[O1 <: Order with PriceCriteria[O2] with NonPriceCriteria[O2], O2 <: Order](): FindBestPricedOrder[O1, O2] = {
    new FindBestPricedOrder[O1, O2]()
  }
}