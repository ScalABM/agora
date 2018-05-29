package org.economicsl.agora.markets.auctions.periodic

import org.economicsl.agora.markets.auctions.orderbooks.SortedBidOrderBook
import org.economicsl.agora.markets.tradables.{LimitPrice, MultiUnit, Quantity, SingleUnit}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder

/** Class defining a single-unit ascending price auction.
  *
  * Single-unit, ascending price auctions are also called "English" auctions in the literature.
  *
  */
class FirstPriceSealedBidSingleUnitAuction {

  def

  def place(order: BidOrder with LimitPrice with Persistent with SingleUnit): Unit = {
    orderBook = orderBook + (order.issuer -> order)
  }

  private[this] @volatile var orderBook: SortedBidOrderBook[BidOrder with LimitPrice with Persistent with SingleUnit]

}
