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
package markets.onesided.matching

import java.util.UUID

import markets.orderbooks
import markets.tradables.orders.{NonPriceCriteria, Order, PriceCriteria}


/** Class defining a `MatchingFunction` that finds the first acceptable `Order` in an `OrderBook`.
  *
  * @tparam O1 the type of `Order` instances that are potential matches.
  * @tparam O2 the type of `Order` that should be matched.
  */
class BestPriceMatchingFunction[O1 <: Order, O2 <: Order with PriceCriteria[O1] with NonPriceCriteria[O1]]
  extends FindFirstMatchingFunction[O1, O2] {

  /** Matches a given `Order` with the first acceptable `Order` found in some `OrderBook`.
    *
    * @param order an `Order` of type `O2` in search of a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if no acceptable `Order` is found in the `orderBook`; `Some(order, matchingOrder)` otherwise.
    * @note Worst case performance of this matching function is `O(n)` where `n` is the number of `Order` instances
    *       contained in the `orderBook` (however for an `order` which has `nonPriceCriteria=None`, the worst case
    *       performance is `O(1)`).  Depending on the type of `orderBook`, the result of this `MatchingFunction`
    *       may be non-deterministic.
    * @todo this function should only be used with a `SortedOrderBook` whose contents are sorted on `limit` price. How
    *       can I impose these constraints on the `orderBook` argument?
    */
  override def apply(order: O2, orderBook: orderbooks.OrderBook[O1, collection.GenMap[UUID, O1]]): Option[(O2, O1)] = {
    order.nonPriceCriteria match {  // when non-price criteria are present, we need to search the order book...
      case Some(_) => super.apply(order, orderBook)
      case None => orderBook.headOption match {  // when only price criteria are present, we only need to check the head of the order book...
        case Some(matchingOrder) => Some(order, matchingOrder)
        case None => None
      }
    }
  }

}