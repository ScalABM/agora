package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.parallel


class ParOrderBook[+O <: Order with Persistent] private(initialOrders: parallel.immutable.ParHashMap[UUID, O], tradable: Tradable)
  extends GenOrderBook[O, parallel.immutable.ParHashMap[UUID, O]]{

  def +[U >: O](issuer: UUID, order: U): ParOrderBook[U] = {
    new ParOrderBook[U](existingOrders + (issuer -> order), tradable)
  }

  def -(issuer: UUID): ParOrderBook[O] = {
    new ParOrderBook[O](existingOrders - issuer, tradable)
  }

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, O)) => Boolean): Option[parallel.immutable.ParIterable[(UUID, O)]] = {
    val acceptableOrders = existingOrders filter p
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  protected val existingOrders: parallel.immutable.ParHashMap[UUID, O] = initialOrders

}


object ParOrderBook {

  def apply[O <: Order with Persistent](tradable: Tradable): ParOrderBook[O] = {
    new ParOrderBook(parallel.immutable.ParHashMap.empty[UUID, O], tradable)
  }

}
