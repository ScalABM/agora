package org.economicsl.agora.markets.auctions.continuous

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.TwoSidedPostedPriceLike
import org.economicsl.agora.markets.auctions.orderbooks.{GenOrderBook, OrderBook, ParOrderBook}
import org.economicsl.agora.markets.tradables.SingleUnit
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


trait GenDoubleAuction[A <: AskOrder, AB <: GenOrderBook[A with Persistent, _],
                       B <: BidOrder, BB <: GenOrderBook[B with Persistent, _]]
  extends TwoSidedPostedPriceLike[A with Persistent, B with Persistent] {

  def fill(order: A): Option[Fill]

  def fill(order: B): Option[Fill]

  protected def askOrderBook: AB

  protected def bidOrderBook: BB

}


trait DoubleAuction[A <: AskOrder, AB <: OrderBook[A with Persistent],
                    B <: BidOrder, BB <: OrderBook[B with Persistent]]
  extends GenDoubleAuction[A, AB, B, BB] {

}


trait ParDoubleAuction[A <: AskOrder, AB <: ParOrderBook[A with Persistent],
                       B <: BidOrder, BB <: ParOrderBook[B with Persistent]]
  extends GenDoubleAuction[A, AB, B, BB] {

}


trait SingleUnitDoubleAuction[A <: AskOrder with SingleUnit, AB <: OrderBook[A with Persistent with SingleUnit],
                              B <: BidOrder with SingleUnit, BB <: OrderBook[B with Persistent with SingleUnit]]
  extends DoubleAuction[A, AB, B, BB] {

  final def fill(order: A): Option[Fill] = askOrderMatchingRule(order, bidOrderBook) {
    case Some(issuer, bidOrder) =>
      bidOrderBook = bidOrderBook - issuer  // SIDE EFFECT!
      val price = askOrderPricingRule(order, bidOrder)
      performance.addValue(surplus(order, bidOrder))  // SIDE EFFECT!
      Some(new Fill(order, bidOrder, price))
    case None => if (order.isPersistent) { askOrderBook = askOrderBook + (order.issuer -> order); None } else None
  }

  final def fill(order: B): Option[Fill] = bidOrderMatchingRule(order, askOrderBook) {
    case Some(issuer, askOrder) =>
      askOrderBook = askOrderBook - issuer  // SIDE EFFECT!
      val price = bidOrderPricingRule(order, askOrder)
      performance.addValue(surplus(askorder, order))  // SIDE EFFECT!
      Some(new Fill(askOrder, order, price))
    case None => if (order.isPersistent) { bidOrderBook = bidOrderBook + (order.issuer -> order); None } else None
  }

}