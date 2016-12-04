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
import org.economicsl.agora.markets.auctions.matching.FindBestPricedOrder
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{SortedAskOrderBook, SortedBidOrderBook}
import org.economicsl.agora.markets.auctions.pricing.ExistingOrderPricingRule
import org.economicsl.agora.markets.tradables.{SingleUnit, Tradable}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


class GodeSunderDoubleAuction(val tradable: Tradable)
  extends TwoSidedPostedPriceAuction[LimitAskOrder with SingleUnit, SortedAskOrderBook[LimitAskOrder with Persistent with SingleUnit],
                                     LimitBidOrder with SingleUnit, SortedBidOrderBook[LimitBidOrder with Persistent with SingleUnit]] {

  /** Fill an incoming `LimitAskOrder`.
    *
    * @param order a `LimitAskOrder` instance.
    * @return
    */
  final def fill(order: LimitAskOrder with SingleUnit): Option[Fill] = {
    buyerPostedPriceAuction.fill(order) match {
      case result @ Some(fill) => clear(); result  // SIDE EFFECT!
      case None => order match {
        case unMatchedOrder: Persistent => place(unMatchedOrder); None
        case _ => None
      }
    }
  }

  /** Fill an incoming `LimitBidOrder`.
    *
    * @param order a `LimitBidOrder` instance.
    * @return
    */
  final def fill(order: LimitBidOrder with SingleUnit): Option[Fill] = {
    sellerPostedPriceAuction.fill(order) match {
      case result@Some(fill) => clear(); result // SIDE EFFECT!
      case None => order match {
        case unMatchedOrder: Persistent => place(unMatchedOrder); None
        case _ => None
      }
    }
  }

  /** An instance of `BuyerPostedPriceAuction` used to fill incoming `AskOrder` instances. */
  protected val buyerPostedPriceAuction = {
    val bidOrderBook = SortedBidOrderBook[LimitBidOrder with Persistent with SingleUnit](tradable)
    val askOrderMatchingRule = FindBestPricedOrder[LimitAskOrder with SingleUnit, LimitBidOrder with Persistent with SingleUnit]()
    val askOrderPricingRule = ExistingOrderPricingRule()
    BuyerPostedPriceAuction(bidOrderBook, askOrderMatchingRule, askOrderPricingRule)
  }

  /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
  protected val sellerPostedPriceAuction = {
    val askOrderBook = SortedAskOrderBook[LimitAskOrder with Persistent with SingleUnit](tradable)
    val bidOrderMatchingRule = FindBestPricedOrder[LimitBidOrder with SingleUnit, LimitAskOrder with Persistent with SingleUnit]()
    val bidOrderPricingRule = ExistingOrderPricingRule()
    SellerPostedPriceAuction(askOrderBook, bidOrderMatchingRule, bidOrderPricingRule)
  }

}


object GodeSunderDoubleAuction {

  /** Create an instance of a `GodeSunderDoubleAuction`.
    *
    * @param tradable
    * @return an instance of a `GodeSunderDoubleAuction` for a particular `Tradable`
    */
  def apply(tradable: Tradable): GodeSunderDoubleAuction = new GodeSunderDoubleAuction(tradable)

}