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
import org.economicsl.agora.markets.tradables.Price


/** Class for modeling a continuous, two-sided posted price auction mechanism.
  *
  * @param askOrderBook
  * @param askOrderMatchingRule
  * @param askOrderPricingRule
  * @param bidOrderBook
  * @param bidOrderMatchingRule
  * @param bidOrderPricingRule
  * @tparam A
  * @tparam AB
  * @tparam B
  * @tparam BB
  */
class TwoSidedPostedPriceAuction[A <: AskOrder, AB <: AskOrderBook[A], B <: BidOrder, BB <: BidOrderBook[B]]
                                (askOrderBook: AB, askOrderMatchingRule: (A, BB) => Option[B], askOrderPricingRule: (A, B) => Price,
                                 bidOrderBook: BB, bidOrderMatchingRule: (B, AB) => Option[A], bidOrderPricingRule: (B, A) => Price)
  extends TwoSidedAuctionLike[A, B] {

  require(askOrderBook.tradable == bidOrderBook.tradable, "Order books must store orders for the same Tradable!")

  /** Cancel an existing `AskOrder` and remove it from the `AskOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: A): Option[A] = sellerPostedPriceAuction.cancel(order)

  /** Cancel an existing `BidOrder` and remove it from the `BidOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: B): Option[B] = buyerPostedPriceAuction.cancel(order)

  /** Fill an `AskOrder`.
    *
    * @param order
    * @return
    */
  final def fill(order: A): Option[Fill] = buyerPostedPriceAuction.fill(order) match {
    case fills @ Some(_) => fills
    case None => place(order); None
  }

  /** Fill a `Bidorder`.
    *
    * @param order
    * @return
    */
  final def fill(order: B): Option[Fill] = sellerPostedPriceAuction.fill(order) match {
    case fills @ Some(_) => fills
    case None => place(order); None
  }

  /** Add an `AskOrder` to the `AskOrderBook`.
    *
    * @param order
    */
  final def place(order: A): Unit = sellerPostedPriceAuction.place(order)

  /** Add a `BidOrder` to the `BidOrderBook`.
    *
    * @param order
    */
  final def place(order: B): Unit = buyerPostedPriceAuction.place(order)

  private[this] val buyerPostedPriceAuction = {
    BuyerPostedPriceAuction(bidOrderBook, askOrderMatchingRule, askOrderPricingRule)
  }

  private[this] val sellerPostedPriceAuction = {
    SellerPostedPriceAuction(askOrderBook, bidOrderMatchingRule, bidOrderPricingRule)
  }

}


object TwoSidedPostedPriceAuction {

  /** Create an instance of a `TwoSidedPostedPriceAuction`.
    *
    * @param askOrderBook
    * @param askOrderMatchingRule
    * @param askOrderPricingRule
    * @param bidOrderBook
    * @param bidOrderMatchingRule
    * @param bidOrderPricingRule
    * @tparam A
    * @tparam AB
    * @tparam B
    * @tparam BB
    * @return an instance of a `TwoSidedPostedPriceAuction`.
    */
  def apply[A <: AskOrder, AB <: AskOrderBook[A], B <: BidOrder, BB <: BidOrderBook[B]]
           (askOrderBook: AB, askOrderMatchingRule: (A, BB) => Option[B], askOrderPricingRule: (A, B) => Price,
            bidOrderBook: BB, bidOrderMatchingRule: (B, AB) => Option[A], bidOrderPricingRule: (B, A) => Price)
            : TwoSidedPostedPriceAuction[A, AB, B, BB] = {
    new TwoSidedPostedPriceAuction[A, AB, B, BB](askOrderBook, askOrderMatchingRule, askOrderPricingRule,
                                                 bidOrderBook, bidOrderMatchingRule, bidOrderPricingRule)
  }

}
