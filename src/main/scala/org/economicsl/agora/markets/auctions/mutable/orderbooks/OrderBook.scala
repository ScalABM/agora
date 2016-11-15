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

import java.util.UUID

import org.economicsl.agora.markets.auctions
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}
import org.economicsl.agora.markets.tradables.Tradable

import scala.collection.mutable


/** Class for modeling an `OrderBook`.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam O type of `Order` stored in the order book.
  */
class OrderBook[O <: Order with Persistent](val tradable: Tradable) extends auctions.orderbooks.OrderBookLike[O]
  with ExistingOrders[O] {

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: ((UUID, O)) => Boolean): Option[Iterable[(UUID, O)]] = {
    val filteredOrders = existingOrders.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: ((UUID, O)) => Boolean): Option[(UUID, O)] = existingOrders.find(p)

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[(UUID, O)] = existingOrders.headOption

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation.
    */
  def reduce[O1 >: O](op: ((UUID, O1), (UUID, O1)) => (UUID, O1)): Option[(UUID, O1)] = existingOrders.reduceOption(op)

  /* Protected at the package level for testing purposes. */
  protected[orderbooks] val existingOrders = mutable.HashMap.empty[UUID, O]

}


object OrderBook {

  def apply[O <: Order with Persistent](tradable: Tradable): OrderBook[O] = new OrderBook[O](tradable)

}