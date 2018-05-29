package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder

import scala.collection.immutable


/** Can not extend OrderBook without violating Liskov substitution principle. */
class SortedBidOrderBook[B <: BidOrder with Persistent] private(initialOrders: immutable.TreeSet[(UUID, B)], tradable: Tradable)
                                                               (implicit ordering: Ordering[(UUID, B)])
  extends OrderBookLike[B] {

  def + (issuer: UUID, order: B): SortedBidOrderBook[B] = {
    new SortedBidOrderBook(existingOrders + ((issuer, order)), tradable)(ordering)
  }

  def - (issuer: UUID, order: B): SortedBidOrderBook[B] = {
    new SortedBidOrderBook(existingOrders - ((issuer, order)), tradable)(ordering)
  }

  /** Filter the `SortedOrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, B)) => Boolean): Option[immutable.TreeSet[(UUID, B)]] = {
    val acceptableOrders = existingOrders.filter(p)
    if (acceptableOrders.isEmpty) None else Some(acceptableOrders)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: ((UUID, B)) => Boolean): Option[(UUID, B)] = existingOrders.find(p)

  /** Applies a binary operator to a start value and all existing orders of the `OrderBook`, going left to right.
    *
    * @tparam T the return type of the binary operator
    * @note might return different results for different runs, unless the existing orders are sorted or the operator is
    *       associative and commutative.
    */
  def foldLeft[T](z: T)(op: (T, (UUID, B)) => T): T = existingOrders.foldLeft(z)(op)

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[(UUID, B)] = existingOrders.headOption

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` does not contain any `Order` instances; `false`, otherwise.
    */
  def isEmpty: Boolean = existingOrders.isEmpty

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` contains any `Order` instances; `false`, otherwise.
    */
  def nonEmpty: Boolean = existingOrders.nonEmpty

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation. The order in which operations are
    *       performed on elements is unspecified and may be nondeterministic depending on the type of `OrderBook`.
    */
  def reduceOption[U >: B](op: ((UUID, U), (UUID, U)) => (UUID, U)): Option[(UUID, U)] = existingOrders.reduceOption(op)

  protected val existingOrders: immutable.TreeSet[(UUID, B)] = initialOrders

}


object SortedBidOrderBook {

  def apply[B <: BidOrder with Persistent](tradable: Tradable)(implicit ordering: Ordering[(UUID, B)]): SortedBidOrderBook[B] = {
    new SortedBidOrderBook[B](immutable.TreeSet.empty[(UUID, B)], tradable)(ordering)
  }

}
