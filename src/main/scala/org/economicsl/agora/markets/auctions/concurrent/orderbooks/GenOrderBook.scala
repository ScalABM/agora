package org.economicsl.agora.markets.auctions.concurrent.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.concurrent


trait GenOrderBook[O <: Order with Persistent] extends orderbooks.GenOrderBook[O, concurrent.Map[UUID, O]] {

  def add(issuer: UUID, order: O): Unit

  def clear(): Unit
  
  def remove(issuer: UUID): Option[O]

}
