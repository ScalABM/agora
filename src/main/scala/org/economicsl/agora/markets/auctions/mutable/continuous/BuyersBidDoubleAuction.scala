package org.economicsl.agora.markets.auctions.mutable.continuous

import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


/** Class implementing a "Buyer's bid" double auction as described in Satterthwaite and Williams (JET, 1989).
  *
  * @param tradable all `Order` instances must be for the same `Tradable`.
  * @note the "Buyer's Bid" double auction is a variant of the k-Double Auction mechanism described in Satterthwaite
  *       and Williams (JET, 1989). In a "Buyer's bid" double auction, the price for each `Fill` is determined by the
  *       `LimitBidOrder`; all of the profit from each trade accrues to the issuer of the `LimitAskOrder`.  Because the
  *       issuer of a `LimitAskOrder` can not influence the `Fill` price, its dominant strategy is always to truthfully
  *       reveal its private reservation value when issuing a `LimitAskOrder`. The issuer of the 'LimitBidOrder',
  *       however, clearly has an incentive to bid strictly less than its private reservation value.
  */
class BuyersBidDoubleAuction(askOrderBook: AskOrderBook[LimitAskOrder with Persistent],
                             askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
                             bidOrderBook: BidOrderBook[LimitBidOrder with Persistent],
                             bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
                             tradable: Tradable)
  extends KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, 1, tradable)


object BuyersBidDoubleAuction {

  /** Create an instance of a `BuyersBidDoubleAuction`.
    *
    * @param askOrderBook
    * @param askOrderMatchingRule
    * @param bidOrderBook
    * @param bidOrderMatchingRule
    * @param tradable
    * @return an instance of a `BuyersBidDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderBook: AskOrderBook[LimitAskOrder with Persistent],
            askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
            bidOrderBook: BidOrderBook[LimitBidOrder with Persistent],
            bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
            tradable: Tradable): BuyersBidDoubleAuction = {
    new BuyersBidDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, tradable)
  }

  /** Create an instance of a `BuyersBidDoubleAuction`.
    *
    * @param askOrderMatchingRule
    * @param bidOrderMatchingRule
    * @param tradable
    * @return an instance of a `BuyersBidDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderMatchingRule: (LimitAskOrder, BidOrderBook[LimitBidOrder with Persistent]) => Option[LimitBidOrder with Persistent],
            bidOrderMatchingRule: (LimitBidOrder, AskOrderBook[LimitAskOrder with Persistent]) => Option[LimitAskOrder with Persistent],
            tradable: Tradable): BuyersBidDoubleAuction = {
    val askOrderBook = AskOrderBook[LimitAskOrder with Persistent](tradable)
    val bidOrderBook = BidOrderBook[LimitBidOrder with Persistent](tradable)
    new BuyersBidDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, tradable)
  }

}
