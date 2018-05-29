package org.economicsl.agora.markets.auctions

import java.util.UUID

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.orderbooks.GenOrderBook
import org.economicsl.agora.markets.tradables.{MultiUnit, Quantity, SingleUnit}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


sealed trait ReverseAuction[B <: LimitBidOrder with Quantity, A <: LimitAskOrder with Persistent with Quantity] {

  type OB <: GenOrderBook[A, collection.GenIterable[(UUID, A)]]

  def fill(order: B): Option[Fill]

  def place(order: A): Unit

}


trait SingleUnitReverseAuction[B <: LimitBidOrder with SingleUnit, A <: LimitAskOrder with Persistent with SingleUnit]
  extends ReverseAuction[B, A]


trait MultiUnitReverseAuction[B <: LimitBidOrder with MultiUnit, A <: LimitAskOrder with Persistent with MultiUnit]
  extends ReverseAuction[B, A]

