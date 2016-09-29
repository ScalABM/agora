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
  * @tparam O1
  * @tparam O2
  * @tparam OB
  */
class FindFirstMatchingFunction[O1 <: Order, O2 <: Order with Predicate[O1], OB <: generic.OrderBook[O1, collection.GenMap[UUID, O1]]]
  extends MatchingFunction[O1, O2, OB] {

  /** Returns the first acceptable `Order` of type `O1`.
    *
    * @param order an `Order` of type `O2` in search of a match.
    * @param orderBook
    * @return `None` if no suitable `Order` of type `O1` is found in the `orderBook`; `Some(order)` otherwise.
    * @note Worst case performance of this matching mechanism is `O(n)` where `n` is the number of `Order`
    *       instances contained in the `OrderBook`.
    */
  def apply(order: O2, orderBook: OB): Option[O1] = orderBook.find(order.isAcceptable)

}
