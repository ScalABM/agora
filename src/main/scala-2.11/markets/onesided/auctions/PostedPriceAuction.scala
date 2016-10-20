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
package markets.onesided.auctions

import java.util.UUID

import markets.onesided.matching.MatchingFunction
import markets.{Fill, orderbooks}
import markets.onesided.pricing.PricingFunction
import markets.tradables.orders.Order

import scala.collection.GenMap


/** Trait defining the interface for a `PostedPriceAuction`.
  *
  * @tparam O1 the type of `Order` stored in the underlying `OrderBook`.
  * @tparam O2 the type of `Order` filled by the `PostedPriceAuction`.
  */
trait PostedPriceAuction[O1 <: Order, O2 <: Order] {

  def fill(order: O2): Option[Fill]

  def cancel(order: O1): Option[O1] = orderBook.remove(order.uuid)

  def place(order: O1): Unit = orderBook.add(order)

  protected def matchingFunction: MatchingFunction[O1, O2]

  protected def orderBook: orderbooks.OrderBook[O1, GenMap[UUID, O1]]

  protected def pricingFunction: PricingFunction[O1, O2]

}