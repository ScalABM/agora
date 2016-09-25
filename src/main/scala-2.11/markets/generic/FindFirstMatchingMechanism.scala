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
package markets.generic

import markets.orders.{Order, Predicate}


trait FindFirstMatchingMechanism[O1 <: Order, O2 <: Order with Predicate[O1], OB <: OrderBook[O1]]
  extends MatchingMechanism[O1, O2, OB] {

  /** Finds the first acceptable matching `Order` of type `O1` for a particular `Order` of type `O2`.
    *
    * @param order a `Order` of type `O2` in search of a match.
    * @return `None` if no suitable `Order` is found in the `OrderBook`; `Some(order)` otherwise.
    * @note Worst case performance of this matching mechanism is `O(n)` where `n` is the number of `Order`
    *       instances contained in the `OrderBook`.
    */
  def findMatchFor(order: O2): Option[O1] = orderBook.find(order.isAcceptable)

}
