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
package markets.matching.onesided

import java.util.UUID

import markets.orderbooks
import markets.tradables.orders.{Operator, Order}


/** Class defining a `MatchingFunction` that matches an `Order` with its preferred `Order` in an `OrderBook`.
  *
  * @tparam O1 the type of `Order` instances that are potential matches.
  * @tparam O2 the type of `Order` that should be matched.
  */
class MostPreferredMatchingFunction[O1 <: Order, O2 <: Order with Operator[O1]] extends MatchingFunction[O1, O2] {

  /** Matches a given `Order` with its preferred `Order` in some `OrderBook`.
    *
    * @param order an `Order` in search of a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if the `orderBook` is empty; `Some(order, matchingOrder)` otherwise.
    * @note Worst case performance of this matching function is `O(n)` where `n` is the number of `Order` instances
    *       contained in the `orderBook`.
    */
  def apply(order: O2, orderBook: orderbooks.OrderBook[O1, collection.GenMap[UUID, O1]]): Option[(O2, O1)] = {
    orderBook.reduce(order.operator) match {
      case Some(matchingOrder) => Some(order, matchingOrder)
      case None => None
    }
  }

}