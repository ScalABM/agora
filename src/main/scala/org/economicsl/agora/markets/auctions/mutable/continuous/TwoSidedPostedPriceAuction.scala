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
package org.economicsl.agora.markets.auctions.mutable.continuous

import org.economicsl.agora.markets.auctions.TwoSidedAuctionLike
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.{LimitPrice, Quantity}
import org.economicsl.agora.markets.tradables.orders.Persistent


trait TwoSidedPostedPriceAuction[A <: AskOrder with LimitPrice with Quantity, AB <: AskOrderBook[A with Persistent],
                                 B <: BidOrder with LimitPrice with Quantity, BB <: BidOrderBook[B with Persistent]]
  extends TwoSidedAuctionLike[A with Persistent, B with Persistent] {

  /** Fill an incoming `AskOrder`.
    *
    * @param order an `AskOrder` instance.
    * @return
    */
  def fill(order: A): Option[Fill]

  /** Fill an incoming `BidOrder`.
    *
    * @param order a `BidOrder` instance.
    * @return
    */
  def fill(order: B): Option[Fill]

  /** Cancel an existing `AskOrder` and remove it from the `AskOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: A with Persistent): Option[A with Persistent] = sellerPostedPriceAuction.cancel(order)

  /** Cancel an existing `BidOrder` and remove it from the `BidOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: B with Persistent): Option[B with Persistent] = buyerPostedPriceAuction.cancel(order)

  final def clear(): Unit = {
    buyerPostedPriceAuction.clear(); sellerPostedPriceAuction.clear()
  }

  /** Add an `AskOrder` to the `AskOrderBook`.
    *
    * @param order
    */
  final def place(order: A with Persistent): Unit = sellerPostedPriceAuction.place(order)

  /** Adds a `BidOrder` to the `BidOrderBook`.
    *
    * @param order
    */
  final def place(order: B with Persistent): Unit = buyerPostedPriceAuction.place(order)

  /** An instance of `BuyerPostedPriceAuction` used to fill incoming `AskOrder` instances. */
  protected def buyerPostedPriceAuction: BuyerPostedPriceAuction[A, BB, B with Persistent]

  /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
  protected def sellerPostedPriceAuction: SellerPostedPriceAuction[B, AB, A with Persistent]

}
