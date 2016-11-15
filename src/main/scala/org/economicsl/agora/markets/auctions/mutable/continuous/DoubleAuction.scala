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
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder
import org.economicsl.agora.markets.tradables.{Price, Tradable}


class DoubleAuction(askOrderPricingRule: (LimitAskOrder, LimitBidOrder with Persistent) => Price,
                    bidOrderPricingRule: (LimitBidOrder, LimitAskOrder with Persistent) => Price,
                    tradable: Tradable)
  extends TwoSidedPostedPriceAuction[LimitAskOrder, SortedAskOrderBook[LimitAskOrder with Persistent],
                                     LimitBidOrder, SortedBidOrderBook[LimitBidOrder with Persistent]] {

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
    val bidOrderBook = SortedBidOrderBook[LimitBidOrder with Persistent](tradable)
    val askOrderMatchingRule = FindBestPricedOrder[LimitAskOrder, LimitBidOrder with Persistent]()
    BuyerPostedPriceAuction(bidOrderBook, askOrderMatchingRule, askOrderPricingRule)
  }

  /** An instance of `SellerPostedPriceAuction` used to fill incoming `BidOrder` instances. */
  protected val sellerPostedPriceAuction = {
    val askOrderBook = SortedAskOrderBook[LimitAskOrder with Persistent](tradable)
    val bidOrderMatchingRule = FindBestPricedOrder[LimitBidOrder, LimitAskOrder with Persistent]()
    SellerPostedPriceAuction(askOrderBook, bidOrderMatchingRule, bidOrderPricingRule)
  }

}
