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

import java.util.UUID

import markets.orders.Order


/** Trait defining the interface for a `MatchingMechanism`. */
trait MatchingMechanism[O1 <: Order, O2 <: Order, OB <: OrderBook[O1, collection.GenMap[UUID, O1]]] {

  def findMatchFor(order: O2, orderBook: OB): Option[O1]

}
