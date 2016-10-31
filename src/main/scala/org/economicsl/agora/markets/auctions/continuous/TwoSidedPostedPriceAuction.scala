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
package org.economicsl.agora.markets.auctions.continuous

import org.economicsl.agora.markets.auctions.{BuyerPostedPriceAuction, SellerPostedPriceAuction, TwoSidedAuctionLike}
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.Fill


trait TwoSidedPostedPriceAuction[A <: AskOrder, B <: BidOrder]
  extends TwoSidedAuctionLike[A, B] {

  /** Fill an `AskOrder`.
    *
    * @param order
    * @return
    */
  def fill(order: A): Option[Fill] = buyerPostedPriceAuction.fill(order) match {
    case fills @ Some(_) => fills
    case None => place(order); None
  }

  /** Fill a `Bidorder`.
    *
    * @param order
    * @return
    */
  def fill(order: B): Option[Fill] = sellerPostedPriceAuction.fill(order) match {
    case fills @ Some(_) => fills
    case None => place(order); None
  }

  /** Cancel an existing `AskOrder` and remove it from the `OrderBook`. */
  final def cancel(order: A): Option[A] = sellerPostedPriceAuction.cancel(order)

  /** Cancel an existing `BidOrder` and remove it from the `OrderBook`. */
  final def cancel(order: B): Option[B] = buyerPostedPriceAuction.cancel(order)

  /** Add an `AskOrder` to the `OrderBook`. */
  final def place(order: A): Unit = sellerPostedPriceAuction.place(order)

  /** Add a `BidOrder` to the `OrderBook`. */
  final def place(order: B): Unit = buyerPostedPriceAuction.place(order)

  /* Concrete implementation should delegate cancel, fill, and place calls to `BuyerPostedPriceAuction`. */
  protected def buyerPostedPriceAuction: BuyerPostedPriceAuction[A, B]

  /* Concrete implementation should delegate cancel, fill, and place calls to `SellerPostedPriceAuction`. */
  protected def sellerPostedPriceAuction: SellerPostedPriceAuction[B, A]

}
