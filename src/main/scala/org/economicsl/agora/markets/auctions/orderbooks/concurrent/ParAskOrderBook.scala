package org.economicsl.agora.markets.auctions.orderbooks.concurrent

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks.GenOrderBook
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder

import scala.collection.parallel


class ConcurrentParAskOrderBook[A <: AskOrder with Persistent](tradable: Tradable)
  extends GenOrderBook[A, parallel.mutable.ParTrieMap[UUID, A]]{

  def add(issuer: UUID, order: A): Unit = existingOrders += (issuer -> order)

  def remove(issuer: UUID): Option[A] = existingOrders.remove(issuer)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, A)) => Boolean): Option[parallel.mutable.ParTrieMap[UUID, A]] = {
    val acceptableOrders = existingOrders.filter(p)
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  protected val existingOrders: parallel.mutable.ParTrieMap[UUID, A] = parallel.mutable.ParTrieMap.empty[UUID, A]

}


object ConcurrentParAskOrderBook {

  def apply[A <: AskOrder with Persistent](tradable: Tradable): ConcurrentParAskOrderBook[A] = {
    new ConcurrentParAskOrderBook(tradable)
  }

}
