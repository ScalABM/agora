package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.Persistent

import scala.collection.immutable


/** Can not extend OrderBook without violating Liskov substitution principle. */
class SortedAskOrderBook[A <: AskOrder with Persistent] private(initialOrders: immutable.TreeSet[(UUID, A)], tradable: Tradable)
                                                               (implicit ordering: Ordering[(UUID, A)])
  extends GenOrderBook[A, immutable.TreeSet[(UUID, A)]] {

  def + (issuer: UUID, order: A): SortedAskOrderBook[A] = {
    new SortedAskOrderBook(existingOrders + ((issuer, order)), tradable)(ordering)
  }

  def - (issuer: UUID, order: A): SortedAskOrderBook[A] = {
    new SortedAskOrderBook(existingOrders - ((issuer, order)), tradable)(ordering)
  }

  /** Filter the `SortedOrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, A)) => Boolean): Option[immutable.TreeSet[(UUID, A)]] = {
    val acceptableOrders = existingOrders.filter(p)
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  protected val existingOrders: immutable.TreeSet[(UUID, A)] = initialOrders

}


object SortedAskOrderBook {

  def apply[A <: AskOrder with Persistent](tradable: Tradable): SortedAskOrderBook[A] = {
    new SortedAskOrderBook[A](immutable.TreeSet.empty[(UUID, A)], tradable)
  }

}
