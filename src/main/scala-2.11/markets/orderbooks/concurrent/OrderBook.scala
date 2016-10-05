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
package markets.orderbooks.concurrent

import java.util.UUID

import markets.orderbooks
import markets.tradables.orders.Order
import markets.tradables.Tradable

import scala.collection.immutable


/** Class for modeling an `OrderBook` for use when thread-safe access is required.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam O type of `Order` stored in the `OrderBook`.
  */
class OrderBook[O <: Order](val tradable: Tradable) extends orderbooks.OrderBook[O, immutable.Map[UUID, O]] {

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
  def filter(p: (O) => Boolean): Option[Iterable[O]] = {
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
    * @note returning the head `Order` of the `OrderBook` is an `O(1)` operation.
    */
  def headOption: Option[O] = existingOrders.values.headOption

  /** Remove and return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    * @note removing and returning the head `Order` of the `OrderBook` is an `O(1)` operation.
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
      case residualOrder @ Some(order) => existingOrders = existingOrders - uuid; residualOrder
      case None => None
    }
  }

  /* Protected at package-level for testing; volatile for thread-safety. */
  @volatile protected[orderbooks] var existingOrders = immutable.Map.empty[UUID, O]

}


/** Companion object for `OrderBook`.
  *
  * Used as a factory for creating `OrderBook` instances.
  */
object OrderBook {

  /** Create a `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the `OrderBook`.
    */
  def apply[O <: Order](tradable: Tradable): OrderBook[O] = new OrderBook[O](tradable)

}