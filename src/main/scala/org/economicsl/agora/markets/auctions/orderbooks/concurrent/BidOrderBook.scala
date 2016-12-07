package org.economicsl.agora.markets.auctions.orderbooks.concurrent

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks.{GenOrderBook, concurrent}
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder

import scala.collection.concurrent


class ConcurrentBidOrderBook[B <: BidOrder with Persistent](tradable: Tradable)
  extends GenOrderBook[B, concurrent.TrieMap[UUID, B]]{

  def add(issuer: UUID, order: B): Unit = existingOrders += (issuer -> order)

  def remove(issuer: UUID): Option[B] = existingOrders.remove(issuer)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, B)) => Boolean): Option[concurrent.TrieMap[UUID, B]] = {
    val acceptableOrders = existingOrders.filter(p)
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  protected val existingOrders: concurrent.TrieMap[UUID, B] = concurrent.TrieMap.empty[UUID, B]

}


object ConcurrentBidOrderBook {

  def apply[B <: BidOrder with Persistent](tradable: Tradable): ConcurrentBidOrderBook[B] = {
    new ConcurrentBidOrderBook(tradable)
  }

}
