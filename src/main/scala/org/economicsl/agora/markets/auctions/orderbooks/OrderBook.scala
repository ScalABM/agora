/*
Copyright 2016 ScalABM

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.annotation.unchecked.uncheckedVariance


/** Trait defining the interface for an order book.
  *
  * @tparam L
  */
trait OrderBook[+L] {

  def +[O >: L](kv: (UUID, O))(implicit ev: O <:< Order with Persistent): OB

  def -(uuid: UUID): OB

  def clear(): OB

  def contains(uuid: UUID): Boolean = existingOrders.contains(uuid)

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (L) => Boolean): Option[collection.GenIterable[L]]

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (L) => Boolean): Option[L] = {
    existingOrders find { case (_, order) => p(order) } map { case (_, order) => order }
  }

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[L]

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` does not contain any `Order with Persistent` instances; `false`, otherwise.
    */
  def isEmpty: Boolean

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` contains any `Order with Persistent` instances; `false`, otherwise.
    */
  def nonEmpty: Boolean

  /** Applies a binary operator to a start value and all existing orders of the `OrderBook`, going left to right.
    *
    * @tparam P the return type of the binary operator
    * @note might return different results for different runs, unless the existing orders are sorted or the operator is
    *       associative and commutative.
    */
  def foldLeft[P](z: P)(op: (P, L) => P): P

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *        `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation. The order in which operations are
    *       performed on elements is unspecified and may be nondeterministic depending on the type of `OrderBook`.
    */
  def reduceOption[O >: L](op: (O, O) => O)(implicit ev: O <:< Order with Persistent): Option[O]

  /** Return the number of existing `Order` instances contained in the `OrderBook`. */
  def size: Int = existingOrders.size

  protected def existingOrders: collection.GenMap[UUID, L]

}