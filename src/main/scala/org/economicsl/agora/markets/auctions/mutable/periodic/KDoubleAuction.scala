package org.economicsl.agora.markets.auctions.mutable.periodic

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.matching.FindFirstAcceptableMatches
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{SortedAskOrderBook, SortedBidOrderBook}
import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricing
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


/** Class implementing a periodic, k-Double Auction.
  *
  * @param k
  * @param tradable
  */
class KDoubleAuction(val k: Double, val tradable: Tradable)
  extends TwoSidedPostedPriceAuction[LimitAskOrder with Persistent, LimitBidOrder with Persistent]{

  def fill(): Option[Iterable[Fill]] = matchingRule(askOrderBook, bidOrderBook) match {
    case Some(matchedOrders) =>
      val (highestAskOrder, lowestBidOrder) = matchedOrders.last
      val price = pricingRule(highestAskOrder, lowestBidOrder)
      val fills = matchedOrders.map { case (askOrder, bidOrder) =>
        val quantity = math.min(askOrder.quantity, bidOrder.quantity)  // assumes that unfilled portion is just cancelled!
        new Fill(bidOrder.issuer, askOrder.issuer, price, quantity, tradable)
      }
      Some(fills)
    case None => None
  }

  protected val askOrderBook = SortedAskOrderBook[LimitAskOrder with Persistent](tradable)

  protected val bidOrderBook = SortedBidOrderBook[LimitBidOrder with Persistent](tradable)

  private[this] val matchingRule = {
    FindFirstAcceptableMatches[LimitAskOrder with Persistent, LimitBidOrder with Persistent]()
  }

  private[this] val pricingRule = WeightedAveragePricing(1-k)

}


object KDoubleAuction {

  def apply(k: Double, tradable: Tradable): KDoubleAuction = new KDoubleAuction(k, tradable)

}