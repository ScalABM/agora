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
package markets.matching

import java.util.UUID

import markets.generic
import markets.orders.{Order, Predicate}


/** Class defining a `MatchingFunction` that finds the first acceptable `Order` in an `OrderBook`.
  *
  * @tparam O1 the type of `Order` instances that are potential matches.
  * @tparam O2 the type of `Order` that should be matched.
  * @tparam OB the type of `OrderBook` containing the potential matching `Order` instances.
  * @note Worst case performance of this matching mechanism is `O(n)` where `n` is the number of `Order` instances
  *       contained in the `orderBook`.  Depending on the type of `OrderBook` used, the result of this
  *       `MatchingFunction` may be non-deterministic.
  */
class FindFirstMatchingFunction[O1 <: Order, O2 <: Order with Predicate[O1], OB <: generic.OrderBook[O1, collection.GenMap[UUID, O1]]]
  extends MatchingFunction[O1, O2, OB] {

  /** Matches a given `Order` with the first acceptable `Order` found in some `OrderBook`.
    *
    * @param order an `Order` of type `O2` in search of a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if no acceptable `Order` is found in the `orderBook`; `Some(order)` otherwise.
    */
  def apply(order: O2, orderBook: OB): Option[O1] = orderBook.find(order.isAcceptable)

}
