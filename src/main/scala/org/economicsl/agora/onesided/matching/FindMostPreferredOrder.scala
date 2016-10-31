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
package org.economicsl.agora.onesided.matching

import org.economicsl.agora.orderbooks
import org.economicsl.agora.tradables.orders.{Operator, Order}


/** Class defining a `MatchingFunction` that matches an `Order` with its preferred `Order` in an `OrderBook`.
  *
  * @tparam O1 the type of `Order` instances that should be matched by the `MatchingFunction`.
  * @tparam O2 the type of `Order` instances that are potential matches and are stored in the `OrderBook`.
  */
class FindMostPreferredOrder[-O1 <: Order with Operator[O2], O2 <: Order]
  extends MatchingFunction[O1, orderbooks.OrderBookLike[O2], O2] {

  /** Matches a given `Order` by reducing the `Order` instances in some `OrderBook` using some binary operator.
    *
    * @param order an `Order` in search of a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if the `orderBook` is empty; `Some(matchingOrder)` otherwise.
    * @note Worst case performance of this matching function is `O(n)` where `n` is the number of `Order` instances
    *       contained in the `orderBook`.
    */
  def apply(order: O1, orderBook: orderbooks.OrderBookLike[O2]): Option[O2] = {
    orderBook.reduce(order.operator)
  }

}