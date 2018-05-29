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
package org.economicsl.agora.markets.auctions.concurrent.continuous

import java.util.UUID

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.concurrent.orderbooks
import org.economicsl.agora.markets.auctions.continuous.PostedPriceAuctionLike
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}
import org.economicsl.agora.markets.tradables.{LimitPrice, Quantity}

import scala.collection.concurrent


/** Trait defining a `PostedPriceAuction`.
  *
  * @tparam O2 the type of `Order` instances that should be filled by the `PostedPriceAuction`.
  */
trait PostedPriceAuction[O1 <: Order with LimitPrice with Quantity, O2 <: Order with LimitPrice with Persistent with Quantity]
  extends PostedPriceAuctionLike[O2, concurrent.TrieMap[UUID, O2]] {

  final def cancel(order: O2): Option[O2] = orderBook.remove(order.issuer)

  final def clear(): Unit = orderBook.clear()

  def fill(order: O1): Option[Fill]

  final def place(order: O2): Unit = orderBook.add(order.issuer, order)

  protected def orderBook: orderbooks.GenOrderBook[O2]

}