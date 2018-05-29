package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.orderbooks.SortedAskOrderBook
import org.economicsl.agora.markets.tradables.SingleUnit
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


trait SingleUnitDescendingPriceReverseAuction[B <: LimitBidOrder with SingleUnit, A <: LimitAskOrder with Persistent with SingleUnit]
  extends SingleUnitReverseAuction[B, A] with DescendingAskOrders[A] {

  type OB = SortedAskOrderBook[A]

  final def fill(order: B): Option[Fill] = findMatchFor(order, orderBook) map {
    case (_, askOrder) =>
      orderBook = orderBook - (askOrder.issuer, askOrder) // SIDE EFFECT!
    val price = formPrice(askOrder, order)
      new Fill(askOrder, order, price, 1)
  }

}
