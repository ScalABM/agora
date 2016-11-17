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

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricing
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


/** Class implementing a k-Double Auction as described in [[http://www.sciencedirect.com/science/article/pii/002205318990121X Satterthwaite and Williams (JET, 1989)]].
  *
  * @param k the value of `k` determines the division of gains from trade between the buyer and the seller. A value of `k` in (0, 1) implies that both the buyer and the seller have influence over the price at which trade occurs. If `k=0`, then the seller sets the price unilaterally; at the other extreme, if `k=1`, the the buyer sets the price unilaterally.
  */
class KDoubleAuction(askOrderBook: AskOrderBook[LimitAskOrder with Persistent],
                     askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
                     bidOrderBook: BidOrderBook[LimitBidOrder with Persistent],
                     bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
                     val k: Double,
                     val tradable: Tradable)
  extends TwoSidedPostedPriceAuction[LimitAskOrder, AskOrderBook[LimitAskOrder with Persistent],
                                     LimitBidOrder, BidOrderBook[LimitBidOrder with Persistent]] {

  require(0 <= k && k <= 1, "The value of k must be in the unit interval (i.e., [0, 1]).")


  /** Fill an incoming `LimitAskOrder`.
    *
    * @param order a `LimitAskOrder` instance.
    * @return
    */
  final def fill(order: LimitAskOrder): Option[Fill] = {
    val result = buyerPostedPriceAuction.fill(order)
    result match {
      case Some(fills) => result
      case None => order match {
        case unfilledOrder: LimitAskOrder with Persistent => place(unfilledOrder); result
        case _ => result
      }
    }
  }

  /** Fill an incoming `LimitBidOrder`.
    *
    * @param order a `LimitBidOrder` instance.
    * @return
    */
  final def fill(order: LimitBidOrder): Option[Fill] = {
    val result = sellerPostedPriceAuction.fill(order)
    result match {
      case Some(fills) => result
      case None => order match {
        case unfilledOrder: LimitBidOrder with Persistent => place(unfilledOrder); result
        case _ => None
      }
    }
  }

  /** An instance of `BuyerPostedPriceAuction` used to fill incoming `AskOrder` instances. */
  protected val buyerPostedPriceAuction = {
    val askOrderPricingRule = WeightedAveragePricing(1-k)
    BuyerPostedPriceAuction(bidOrderBook, askOrderMatchingRule, askOrderPricingRule)
  }

  /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
  protected val sellerPostedPriceAuction = {
    val bidOrderPricingRule = WeightedAveragePricing(k)
    SellerPostedPriceAuction(askOrderBook, bidOrderMatchingRule, bidOrderPricingRule)
  }

}


object KDoubleAuction {

  /** Create an instance of a `KDoubleAuction`.
    *
    * @param askOrderBook
    * @param askOrderMatchingRule
    * @param bidOrderBook
    * @param bidOrderMatchingRule
    * @param k
    * @param tradable
    * @return an instance of a `KDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderBook: AskOrderBook[LimitAskOrder with Persistent],
            askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
            bidOrderBook: BidOrderBook[LimitBidOrder with Persistent],
            bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
            k: Double,
            tradable: Tradable): KDoubleAuction = {
    new KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, k, tradable)
  }

  /** Create an instance of a `KDoubleAuction`.
    *
    * @param askOrderMatchingRule
    * @param bidOrderMatchingRule
    * @param k
    * @param tradable
    * @return an instance of a `KDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
            bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
            k: Double,
            tradable: Tradable): KDoubleAuction = {
    val askOrderBook = AskOrderBook[LimitAskOrder with Persistent](tradable)
    val bidOrderBook = BidOrderBook[LimitBidOrder with Persistent](tradable)
    new KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, k, tradable)
  }

}