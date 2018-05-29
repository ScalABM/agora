package org.economicsl.agora.markets.auctions.periodic

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.TwoSidedPostedPriceLike
import org.economicsl.agora.markets.auctions.orderbooks.GenOrderBook
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


trait GenDoubleAuction[A <: AskOrder, AB <: GenOrderBook[A with Persistent, _],
                       B <: BidOrder, BB <: GenOrderBook[B with Persistent, _]]
  extends TwoSidedPostedPriceLike[A with Persistent, B with Persistent] {

  def fill(): Option[collection.GenIterable[Fill]]

  protected def askOrderBook: AB

  protected def bidOrderBook: BB

}
