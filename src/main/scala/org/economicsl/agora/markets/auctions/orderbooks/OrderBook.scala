package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.immutable


class OrderBook[+O <: Order with Persistent] private(initialOrders: immutable.HashMap[UUID, O], tradable: Tradable)
  extends GenOrderBook[O, immutable.HashMap[UUID, O]]{

  def +[U >: O](issuer: UUID, order: U): OrderBook[U] = {
    new OrderBook[U](existingOrders updated(issuer, order), tradable)
  }

  def -(issuer: UUID): OrderBook[O] = new OrderBook[O](existingOrders - issuer, tradable)

  def empty: OrderBook[O] = new OrderBook[O](existingOrders.empty, tradable)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, O)) => Boolean): Option[immutable.Iterable[(UUID, O)]] = {
    val acceptableOrders = existingOrders.filter(p)
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  protected val existingOrders: immutable.HashMap[UUID, O] = initialOrders

}


object OrderBook {

  def apply[O <: Order with Persistent](tradable: Tradable): OrderBook[O] = {
    new OrderBook(immutable.HashMap.empty[UUID, O], tradable)
  }

}
