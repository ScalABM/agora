package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.auctions.orderbooks.{SortedAskOrderBook, SortedBidOrderBook}
import org.economicsl.agora.markets.tradables.{Quantity, Tradable}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


trait AscendingBidOrders[A <: LimitAskOrder with Quantity, B <: LimitBidOrder with Persistent with Quantity] {
  this: Auction[A, B] =>

  type OB = SortedBidOrderBook[B]

  def tradable: Tradable

  protected def orderBook: OB

}


trait DescendingAskOrders[B <: A <: LimitAskOrder with Persistent with Quantity] {
this: ReverseAuction[_, A] =>

def tradable: Tradable

protected def orderBook: SortedAskOrderBook[A]

}


trait SortedOrders[A <: LimitAskOrder with Persistent with Quantity, B <: LimitBidOrder with Persistent with Quantity] {
  this: DoubleAuction[A, B] =>

  def tradable: Tradable

  protected def askOrderBook: SortedAskOrderBook[A]

  protected def bidOrderBook: SortedBidOrderBook[B]

}