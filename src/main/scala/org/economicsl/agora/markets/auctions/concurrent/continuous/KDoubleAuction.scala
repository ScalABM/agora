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

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.concurrent.orderbooks._
import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricingRule
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder
import org.economicsl.agora.markets.tradables.{Quantity, Tradable}


/** Class implementing a k-Double Auction as described in [[http://www.sciencedirect.com/science/article/pii/002205318990121X Satterthwaite and Williams (JET, 1989)]].
  *
  * @param k the value of `k` determines the division of gains from trade between the buyer and the seller. A value of
  *          `k` in (0, 1) implies that both the buyer and the seller have influence over the price at which trade
  *          occurs. If `k=0`, then the seller sets the price unilaterally; at the other extreme, if `k=1`, the buyer
  *          sets the price unilaterally.
  */
abstract class KDoubleAuction[AB <: GenOrderBook[LimitAskOrder with Persistent with Quantity],
                              BB <: GenOrderBook[LimitBidOrder with Persistent with Quantity]]
                             (askOrderMatchingRule: (LimitAskOrder with Quantity, BB) => Option[LimitBidOrder with Persistent with Quantity],
                              bidOrderMatchingRule: (LimitBidOrder with Quantity, AB) => Option[LimitAskOrder with Persistent with Quantity],
                              val k: Double,
                              val tradable: Tradable)
  extends DoubleAuction[LimitAskOrder with Quantity, AB, LimitBidOrder with Quantity, BB] {

  require(0 <= k && k <= 1, "The value of k must be in the unit interval (i.e., [0, 1]).")


  /** Fill an incoming `LimitAskOrder`.
    *
    * @param order a `LimitAskOrder` instance.
    * @return
    */
  final def fill(order: LimitAskOrder with Quantity): Option[Fill] = {
    val result = buyersAuction.fill(order)
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
  final def fill(order: LimitBidOrder with Quantity): Option[Fill] = {
    val result = sellersAuction.fill(order)
    result match {
      case Some(fills) => result
      case None => order match {
        case unfilledOrder: LimitBidOrder with Persistent => place(unfilledOrder); result
        case _ => None
      }
    }
  }

  /** An instance of `BuyerPostedPriceAuction` used to fill incoming `AskOrder` instances. */
  protected def buyersAuction: BuyerPostedPriceAuction[LimitAskOrder with Quantity, LimitBidOrder with Persistent with Quantity, BB]

  /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
  protected val sellersAuction: SellerPostedPriceAuction[LimitBidOrder with Quantity, LimitAskOrder with Persistent with Quantity, AB]

}


object KDoubleAuction {

  type A = LimitAskOrder with Quantity
  type AB = AskOrderBook[A with Persistent]
  type B = LimitBidOrder with Quantity
  type BB = BidOrderBook[B with Persistent]
  type ParAB = ParAskOrderBook[A with Persistent]
  type ParBB = ParBidOrderBook[B with Persistent]

  /** Create an instance of a `KDoubleAuction`.
    *
    * @param askOrderMatchingRule
    * @param bidOrderMatchingRule
    * @param k
    * @param tradable
    * @return an instance of a `KDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderMatchingRule: (A, BB) => Option[B with Persistent],
            bidOrderMatchingRule: (B, AB) => Option[A with Persistent],
            k: Double,
            tradable: Tradable)
           : KDoubleAuction[AB, BB] = {

    new KDoubleAuction[AB, BB](askOrderMatchingRule, bidOrderMatchingRule, k, tradable) {

      protected val buyersAuction: BuyerPostedPriceAuction[A, BB, B with Persistent] = {
        val askOrderPricingRule = WeightedAveragePricingRule(1-k)
        BuyerPostedPriceAuction(askOrderMatchingRule, askOrderPricingRule, tradable)
      }

      /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
      protected val sellersAuction: SellerPostedPriceAuction[B, AB, A with Persistent] = {
        val bidOrderPricingRule = WeightedAveragePricingRule(k)
        SellerPostedPriceAuction(bidOrderMatchingRule, bidOrderPricingRule, tradable)
      }

    }

  }

  /** Create an instance of a `KDoubleAuction`.
    *
    * @param askOrderMatchingRule
    * @param bidOrderMatchingRule
    * @param k
    * @param tradable
    * @return an instance of a `KDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderMatchingRule: (A, ParBidOrderBook[B with Persistent]) => Option[B with Persistent],
            bidOrderMatchingRule: (B, ParAskOrderBook[A with Persistent]) => Option[A with Persistent],
            k: Double,
            tradable: Tradable)
           : KDoubleAuction[ParAskOrderBook[A with Persistent], ParBidOrderBook[B with Persistent]] = {

    new KDoubleAuction[ParAB, ParBB](askOrderMatchingRule, bidOrderMatchingRule, k, tradable) {

      protected val buyersAuction: BuyerPostedPriceAuction[A, ParBB, B with Persistent] = {
        val askOrderPricingRule = WeightedAveragePricingRule(1-k)
        BuyerPostedPriceAuction(askOrderMatchingRule, askOrderPricingRule, tradable)
      }

      /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
      protected val sellersAuction: SellerPostedPriceAuction[B, ParAB, A with Persistent] = {
        val bidOrderPricingRule = WeightedAveragePricingRule(k)
        SellerPostedPriceAuction(bidOrderMatchingRule, bidOrderPricingRule, tradable)
      }

    }

  }

}