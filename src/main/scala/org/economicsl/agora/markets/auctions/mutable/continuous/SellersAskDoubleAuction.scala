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

import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


/** Class implementing a "Seller's Ask" double auction as described in Satterthwaite and Williams (JET, 1989).
  *
  * @param tradable all `Order` instances must be for the same `Tradable`.
  * @note the "Seller's Ask" double auction is a variant of the k-Double Auction mechanism described in Satterthwaite
  *       and Williams (JET, 1989). In a "Seller's Ask" double auction, the price for each `Fill` is determined by the
  *       `LimitAskOrder`; all of the profit from each trade accrues to the issuer of the `LimitBidOrder`.  Because the
  *       issuer of a `LimitBidOrder` can not influence the `Fill` price, its dominant strategy is always to truthfully
  *       reveal its private reservation value when issuing a `LimitBidOrder`. The issuer of the 'LimitAskOrder',
  *       however, clearly has an incentive to bid strictly more than its private reservation value.
  */
class SellersAskDoubleAuction(askOrderBook: AskOrderBook[LimitAskOrder with Persistent],
                              askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
                              bidOrderBook: BidOrderBook[LimitBidOrder with Persistent],
                              bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
                              tradable: Tradable)
  extends KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, 0, tradable)


/** Companion object for a `SellersAskDoubleAuction`.
  *
  * Contains auxiliary constructors for a `SellersAskDoubleAuction` as well as classes implementing the equilibrium
  * trading rules for the auction derived in Satterthwaite and Williams (JET, 1989).
  */
object SellersAskDoubleAuction {

  /** Create an instance of a `SellersAskDoubleAuction`.
    *
    * @param askOrderBook
    * @param askOrderMatchingRule
    * @param bidOrderBook
    * @param bidOrderMatchingRule
    * @param tradable
    * @return an instance of a `SellersAskDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderBook: AskOrderBook[LimitAskOrder with Persistent],
            askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
            bidOrderBook: BidOrderBook[LimitBidOrder with Persistent],
            bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
            tradable: Tradable): SellersAskDoubleAuction = {
    new SellersAskDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, tradable)
  }

  /** Create an instance of a `SellersAskDoubleAuction`.
    *
    * @param askOrderMatchingRule
    * @param bidOrderMatchingRule
    * @param tradable
    * @return an instance of a `SellersAskDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
            bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
            tradable: Tradable): SellersAskDoubleAuction = {
    val askOrderBook = AskOrderBook[LimitAskOrder with Persistent](tradable)
    val bidOrderBook = BidOrderBook[LimitBidOrder with Persistent](tradable)
    new SellersAskDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, tradable)
  }

}
