package org.economicsl.agora.markets.auctions.orderbooks.concurrent

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks.{GenOrderBook, concurrent}
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder

import scala.collection.concurrent


class AskOrderBook[A <: AskOrder with Persistent](tradable: Tradable)
  extends GenOrderBook[A, concurrent.TrieMap[UUID, A]]{

  def add(issuer: UUID, order: A): Unit = existingOrders += (issuer -> order)

  def remove(issuer: UUID): Option[A] = existingOrders.remove(issuer)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, A)) => Boolean): Option[concurrent.TrieMap[UUID, A]] = {
    val acceptableOrders = existingOrders.filter(p)
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  protected val existingOrders: concurrent.TrieMap[UUID, A] = concurrent.TrieMap.empty[UUID, A]

}


object AskOrderBook {

  def apply[A <: AskOrder with Persistent](tradable: Tradable): AskOrderBook[A] = {
    new AskOrderBook(tradable)
  }

}
