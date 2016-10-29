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
package org.economicsl.agora.onesided.auctions

import java.util.UUID

import org.economicsl.agora.onesided.matching.MatchingFunction
import org.economicsl.agora.{Fill, orderbooks}
import org.economicsl.agora.onesided.pricing.PricingFunction
import org.economicsl.agora.tradables.orders.Order


/** Trait defining the interface for a `PostedPriceAuction`.
  *
  * @tparam O1 the type of `Order` instances that should be filled by the `PostedPriceAuction`.
  * @tparam OB the type of `OrderBook` used to store the potential matches.
  * @tparam O2 the type of `Order` instances that are potential matches and are stored in the `OrderBook`.
  */
trait PostedPriceAuction[O1 <: Order, OB <: orderbooks.OrderBook[O2, collection.GenMap[UUID, O2]], O2 <: Order] {

  def fill(order: O1): Option[Fill]

  def cancel(order: O2): Option[O2] = orderBook.remove(order.uuid)

  def place(order: O2): Unit = orderBook.add(order)

  protected def matchingFunction: MatchingFunction[O1, OB, O2]

  protected def orderBook: OB

  protected def pricingFunction: PricingFunction[O1, O2]

}