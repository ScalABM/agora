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
package org.economicsl.agora.markets.auctions.orderbooks.parallel.concurrent

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks
import org.economicsl.agora.markets.auctions.orderbooks.ExistingOrders
import org.economicsl.agora.markets.tradables.orders.Order
import org.economicsl.agora.markets.tradables.Tradable

import scala.collection.parallel


/** Class for modeling an `OrderBook` for use when thread-safe access is required.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam O type of `Order` stored in the `OrderBook`.
  * @todo Currently the underlying `existingOrders` will use the JVM default ForkJoinTaskSupport object for scheduling
  *       and load-balancing.  This [[http://docs.scala-lang.org/overviews/parallel-collections/configuration.html can be customized]]
  *       but requires some clear thinking about how to expose this functionality to the user.
  */
class OrderBook[O <: Order](val tradable: Tradable)
  extends org.economicsl.agora.markets.auctions.orderbooks.OrderBookLike[O] with ExistingOrders[O, parallel.immutable.ParMap[UUID, O]] {

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    * @note adding an `Order` to the `OrderBook` is an `O(1)` operation.
    */
  def add(order: O): Unit = {
    require(order.tradable == tradable)
    existingOrders.synchronized { existingOrders = existingOrders + (order.uuid -> order) }
  }

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    * @note filtering the `OrderBook` is an `O(n)` operation.
    */
  def filter(p: (O) => Boolean): Option[parallel.ParIterable[O]] = {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    * @note finding an `Order` in the `OrderBook` is an `O(n)` operation.
    */
  def find(p: (O) => Boolean): Option[O] = existingOrders.values.find(p)

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[O] = existingOrders.values.headOption

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation.
    */
  def reduce[O1 >: O](op: (O1, O1) => O1): Option[O1] = existingOrders.values.reduceOption(op)

  /** Remove and return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def remove(): Option[O] = headOption match {
    case Some(order) => remove(order.uuid)
    case None => None
  }

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the `Order` that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the `OrderBook`; `Some(order)` otherwise.
    * @note removing and returning an `Order` from the `OrderBook` is an `O(1)` operation.
    */
  def remove(uuid: UUID): Option[O] = existingOrders.synchronized {
    existingOrders.get(uuid) match {
      case residualOrder@Some(order) => existingOrders = existingOrders - uuid; residualOrder
      case None => None
    }
  }

  /* Protected at package-level for testing; volatile for thread-safety. */
  @volatile protected[orderbooks] var existingOrders = parallel.immutable.ParMap.empty[UUID, O]

}


/** Factory for creating `OrderBook` instances. */
object OrderBook {

  /** Create a `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the `OrderBook`.
    */
  def apply[O <: Order](tradable: Tradable): OrderBook[O] = new OrderBook[O](tradable)

}