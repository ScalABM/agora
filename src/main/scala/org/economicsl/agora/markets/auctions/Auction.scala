package org.economicsl.agora.markets.auctions

import java.util.UUID

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.orderbooks.GenOrderBook
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder
import org.economicsl.agora.markets.tradables.{MultiUnit, Quantity, SingleUnit}


sealed trait Auction[A <: LimitAskOrder with Quantity, B <: LimitBidOrder with Persistent with Quantity] {

  type OB <: GenOrderBook[B, collection.GenIterable[(UUID, B)]]

  def fill(order: A): Option[Fill]

  def place(order: B): Unit

  protected def orderBook: OB

}


trait SingleUnitAuction[A <: LimitAskOrder with SingleUnit, B <: LimitBidOrder with Persistent with SingleUnit]
  extends Auction[A, B]


trait MultiUnitAuction[A <: LimitAskOrder with MultiUnit, B <: LimitBidOrder with Persistent with MultiUnit]
  extends Auction[A, B]
