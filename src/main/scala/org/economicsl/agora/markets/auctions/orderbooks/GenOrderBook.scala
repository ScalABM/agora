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


trait GenOrderBook[+O <: Order with Persistent, +CC <: collection.GenIterable[(UUID, O)]]
  extends OrderBookLike[O] with ExistingOrders[UUID, O, CC] {

  /** Find the first `(UUID, Order)` pair in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `(UUID, Order)` characteristics.
    * @return `None` if no `(UUID, Order)` pair in the `OrderBook` satisfies the predicate; `Some((issuer, order))`
    *         otherwise.
    */
  def find(p: ((UUID, O)) => Boolean): Option[(UUID, O)] = existingOrders.find(p)

  /** Applies a binary operator to a start value and all existing orders of the `OrderBook`, going left to right.
    *
    * @tparam T the return type of the binary operator
    * @note might return different results for different runs, unless the existing orders are sorted or the operator is
    *       associative and commutative.
    */
  def foldLeft[T](z: T)(op: (T, (UUID, O)) => T): T = existingOrders.foldLeft(z)(op)

  /** Return the head `(UUID, Order)` pair in the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[(UUID, O)] = existingOrders.headOption

  /** Boolean flag indicating whether or not the `OrderBook` contains `(UUID, Order)` instances.
    *
    * @return `true`, if the `OrderBook` does not contain any `(UUID, Order)` instances; `false`, otherwise.
    */
  def isEmpty: Boolean = existingOrders.isEmpty

  /** Boolean flag indicating whether or not the `OrderBook` contains `(UUID, Order)` instances.
    *
    * @return `true`, if the `OrderBook` contains any `(UUID, Order)` instances; `false`, otherwise.
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
  def reduceOption[U >: O](op: ((UUID, U), (UUID, U)) => (UUID, U)): Option[(UUID, U)] = existingOrders.reduceOption(op)

}