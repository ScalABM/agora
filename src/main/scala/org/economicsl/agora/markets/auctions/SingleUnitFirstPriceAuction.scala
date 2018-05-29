package org.economicsl.agora.markets.auctions

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks.SortedBidOrderBook
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{Price, SingleUnit, Tradable}
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


class SingleUnitFirstPriceAuction[](tradable: Tradable)
  extends SingleUnitAscendingAuction[LimitAskOrder with SingleUnit, LimitBidOrder with Persistent with SingleUnit] {

  val matchingRule: (LimitAskOrder with SingleUnit, SortedBidOrderBook[LimitBidOrder with Persistent with SingleUnit]) => Option[(UUID, LimitBidOrder with Persistent with SingleUnit)] = {
    (askOrder, bidOrderBook) => bidOrderBook.headOption.filter { case (_, bidOrder) => bidOrder.limit >= askOrder.limit }
  }
  val pricingRule: (LimitAskOrder with SingleUnit, LimitBidOrder with Persistent with SingleUnit) => Price = {
    (_, bidOrder) => bidOrder.limit
  }

}
