package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.immutable.Iterable
import scala.collection.parallel.immutable


class ParHashOrderBook[+L] private(initialOrders: immutable.ParHashMap[UUID, L], tradable: Tradable)
  extends OrderBook[L] {

  def +[O >: L](kv: (UUID, O))(implicit ev: O <:< Order with Persistent): ParHashOrderBook[O] = new ParHashOrderBook[O](existingOrders + kv, tradable)

  def -(uuid: UUID): ParHashOrderBook[L] = new ParHashOrderBook[L](existingOrders - uuid, tradable)

  def clear(): ParHashOrderBook[L] = new ParHashOrderBook[L](immutable.ParHashMap.empty[UUID, L], tradable)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (L) => Boolean): Option[Iterable[L]] = ???

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[L] = ???

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` does not contain any `Order with Persistent` instances; `false`, otherwise.
    */
  def isEmpty: Boolean = ???

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` contains any `Order with Persistent` instances; `false`, otherwise.
    */
  def nonEmpty: Boolean = ???

  /** Applies a binary operator to a start value and all existing orders of the `OrderBook`, going left to right.
    *
    * @tparam P the return type of the binary operator
    * @note might return different results for different runs, unless the existing orders are sorted or the operator is
    *       associative and commutative.
    */
  def foldLeft[P](z: P)(op: (P, L) => P): P = ???

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation. The order in which operations are
    *       performed on elements is unspecified and may be nondeterministic depending on the type of `OrderBook`.
    */
  def reduceOption[O >: L](op: (O, O) => O)(implicit ev: O <:< Order with Persistent): Option[O] = ???

  protected val existingOrders = initialOrders

}


object ParHashOrderBook {

  def apply[L <: Order with Persistent](tradable: Tradable): ParHashOrderBook[L] = {
    new ParHashOrderBook[L](immutable.ParHashMap.empty[UUID, L], tradable)
  }

}