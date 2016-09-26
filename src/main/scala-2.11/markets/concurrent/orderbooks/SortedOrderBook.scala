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
package markets.concurrent.orderbooks

import java.util.UUID

import markets.generic
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.immutable


/** Class for modeling an `OrderBook` when thread-safe access to a `SortedOrderBook` is required.
  *
  * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
  * @tparam O type of `Order` stored in the order book.
  */
class SortedOrderBook[O <: Order](tradable: Tradable)(implicit ordering: Ordering[O])
  extends generic.SortedOrderBook[O](tradable){

  /** Add an `Order` to the `SortedOrderBook`.
    *
    * @param order the `Order` that should be added to the `SortedOrderBook`.
    * @note adding an `Order` to the `SortedOrderBook` is an `O(log n)` operation.
    */
  def add(order: O): Unit = {
    require(order.tradable == tradable)
    synchronized {
      existingOrders = existingOrders + (order.uuid -> order)
      sortedOrders = sortedOrders + order
    }
  }

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (O) => Boolean): Option[Iterable[O]] = existingOrders.synchronized {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Remove and return an existing `Order` from the `SortedOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `SortedOrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note underlying implementation of guarantees that removing and returning an `Order` from the
    *       `SortedOrderBook` is an `O(log n)` operation.
    */
  def remove(uuid: UUID): Option[O] = synchronized {
    existingOrders.get(uuid) match {
      case residualOrder @ Some(order) =>
        sortedOrders = sortedOrders - order
        existingOrders = existingOrders - uuid
        residualOrder
      case None => None
    }
  }

  /* Protected at package-level for testing. */
  protected[orderbooks] var existingOrders = immutable.HashMap.empty[UUID, O]

  /* Protected at package-level for testing. */
  protected[orderbooks] var sortedOrders = immutable.TreeSet.empty[O]

}


/** Factory for creating `SortedOrderBook` instances. */
object SortedOrderBook {

  /** Create a `SortedOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable)(implicit ordering: Ordering[O]): SortedOrderBook[O] = {
    new SortedOrderBook[O](tradable)(ordering)
  }

}