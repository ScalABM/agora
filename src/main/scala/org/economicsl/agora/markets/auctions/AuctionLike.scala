package org.economicsl.agora.markets.auctions

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks.{ExistingOrders, OrderBookLike}
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


trait AuctionLike[O <: Order with Persistent] {

  def place(order: O): Either[Reject, Accept]

  def orderBook: OrderBookLike[O] with ExistingOrders[O, Map[UUID, O]]

}