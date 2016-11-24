package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.immutable


class HashOrderBook[+O <: Order with Persistent] private(initialOrders: immutable.HashMap[UUID, O], tradable: Tradable)
  extends OrderBook[O, HashOrderBook[O]] {

  def +[O1 >: O](kv: (UUID, O1)): HashOrderBook[O1] = new HashOrderBook[O1](existingOrders + kv, tradable)

  def -(uuid: UUID): HashOrderBook[O] = new HashOrderBook[O](existingOrders - uuid, tradable)

  def clear(): HashOrderBook[O] = new HashOrderBook[O](immutable.HashMap.empty[UUID, O], tradable)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (O) => Boolean): Option[immutable.Iterable[O]] = ???

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (O) => Boolean): Option[O] = ???

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[O] = ???

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
  def foldLeft[P](z: P)(op: (P, O) => P): P = ???

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation. The order in which operations are
    *       performed on elements is unspecified and may be nondeterministic depending on the type of `OrderBook`.
    */
  def reduceOption[O1 >: O](op: (O1, O1) => O1): Option[O1] = ???

  protected val existingOrders = initialOrders

}


object HashOrderBook {

  def apply[O <: Order with Persistent](tradable: Tradable): HashOrderBook[O] = {
    new HashOrderBook(immutable.HashMap.empty[UUID, O], tradable)
  }

}