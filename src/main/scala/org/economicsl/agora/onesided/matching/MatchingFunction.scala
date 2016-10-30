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

import java.util.UUID

import org.economicsl.agora.orderbooks
import org.economicsl.agora.tradables.orders.Order


/** Trait defining the interface for a `MatchingFunction`.
  *
  * @tparam O1 the type of `Order` instances that should be matched by the `MatchingFunction`.
  * @tparam OB the type of `OrderBook` used to store the potential matches.
  * @tparam O2 the type of `Order` instances that are potential matches and are stored in the `OrderBook`.
  */
trait MatchingFunction[-O1 <: Order, -OB <: orderbooks.OrderBookLike[O2], +O2 <: Order]
  extends ((O1, OB) => Option[O2]) {

  /** Matches a given `Order` with an existing `Order` from an `OrderBook`.
    *
    * @param order the `Order` that needs a match.
    * @param orderBook an `OrderBook` containing potential matches for the `order`.
    * @return `None` if no suitable `Order` is found in the `orderBook`; `Some(order)` otherwise.
    */
  def apply(order: O1, orderBook: OB): Option[O2]

}
