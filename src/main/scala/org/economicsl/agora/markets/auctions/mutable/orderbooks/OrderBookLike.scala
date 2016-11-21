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
package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.auctions
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


/** Trait defining "read" methods for an `OrderBook`.
  *
  * @tparam O the type of `Order with Persistent` stored in a `OrderBook`.
  */
trait OrderBookLike[O <: Order with Persistent] extends auctions.orderbooks.OrderBookLike[O] {
  this: ExistingOrders[O] =>

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (O) => Boolean): Option[Iterable[O]] = {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (O) => Boolean): Option[O] = existingOrders.values.find(p)

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[O] = existingOrders.values.headOption

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
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation.
    */
  def reduce[O1 >: O](op: (O1, O1) => O1): Option[O1] = existingOrders.values.reduceOption(op)

  /** Return the number of existing `Order` instances contained in the `OrderBook`. */
  def size: Int = existingOrders.size

}


