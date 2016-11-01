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

import org.economicsl.agora.markets.auctions.orderbooks.OrderBookLike
import org.economicsl.agora.markets.tradables.orders.{Operator, Order, Predicate}


/** A `MatchingFunction` that matches an `Order` with its preferred match from a collection of acceptable matches.
  *
  * @tparam O1 the type of `Order` instances that should be matched by the `MatchingFunction`.
  * @tparam O2 the type of `Order` instances that are potential matches and are stored in the `OrderBook`.
  */
class FindMostPreferredAcceptableOrder[-O1 <: Order with Predicate[O2] with Operator[O2], O2 <: Order]
  extends ((O1, OrderBookLike[O2]) => Option[O2]) {

  /** Matches an `Order` with its preferred match from a collection of acceptable matches.
    *
    * @param order an `Order` in search of a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if the `orderBook` is empty; `Some(matchingOrder)` otherwise.
    * @note Worst case performance of this matching function is `O(n)` where `n` is the number of `Order` instances
    *       contained in the `orderBook`.
    */
  def apply(order: O1, orderBook: OrderBookLike[O2]): Option[O2] = {
    orderBook.filter(order.isAcceptable).map(acceptableOrders => acceptableOrders.reduce(order.operator))
  }

}