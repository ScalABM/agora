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

import markets.generic.OrderBook
import markets.orders.Order


/** Trait defining the interface for a `MatchingFunction`.
  *
  * @tparam O1
  * @tparam O2
  * @tparam OB
  */
trait MatchingFunction[O1 <: Order, O2 <: Order, OB <: OrderBook[O1, collection.GenMap[UUID, O1]]]
  extends Function2[O2, OB, Option[O1]] {

  /** blah
    *
    * @param order the `Order` of type `O2` that needs a matching `Order` of type `O1`.
    * @param orderBook an `OrderBook` containing `Order` instances of type `O1`.
    * @return `None` if no suitable `Order` is found in the `orderBook`; `Some(order)` otherwise.
    */
  def apply(order: O2, orderBook: OB): Option[O1]

}
