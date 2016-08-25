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
package markets.orderbooks

import java.util.UUID

import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.immutable


/** Class for modeling an `OrderBook` when thread-safe access to a `SortedOrderBook` is required.
  *
  * @param tradable all `Orders` contained in the `ConcurrentSortedOrderBook` should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  */
class ConcurrentSortedOrderBook[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A])
  extends ConcurrentOrderBook[A](tradable){

  /** Add an `Order` to the `ConcurrentSortedOrderBook`.
    *
    * @param order the `Order` that should be added to the `ConcurrentSortedOrderBook`.
    * @note underlying implementation of guarantees that adding an `Order` to the `ConcurrentSortedOrderBook` is an
    *       `O(log n)` operation.
    */
  override def add(order: A): Unit = {
    super.add(order)
    sortedOrders = sortedOrders + order
  }
  
  /** Return the first `Order` in the `ConcurrentSortedOrderBook`.
    *
    * @return `None` if the `ConcurrentSortedOrderBook` is empty; `Some(order)` otherwise.
    * @note underlying implementation of `sortedOrders` guarantees that returning the first `Order` in the 
    *       `ConcurrentSortedOrderBook` is an `O(1)` operation.
    */
  def headOption: Option[A] = sortedOrders.headOption

  /** Remove and return the first `Order` in the `ConcurrentSortedOrderBook`.
    *
    * @return `None` if the `ConcurrentSortedOrderBook` is empty; `Some(order)` otherwise.
    * @note underlying implementation of `sortedOrders` guarantees that removing and returning the first `Order` in the
    *       `ConcurrentSortedOrderBook` is an `O(log n)` operation.
    */
  def remove(): Option[A] = {
    headOption match {
      case Some(order) => remove(order.uuid)
      case None => None
    }
  }
  
  /** Remove and return an existing `Order` from the `ConcurrentSortedOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `ConcurrentSortedOrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note underlying implementation of guarantees that removing and returning an `Order` from the
    *       `ConcurrentSortedOrderBook` is an `O(log n)` operation.
    */
  override def remove(uuid: UUID): Option[A] = {
    super.remove(uuid) match {
      case residualOrder @ Some(order) =>
        sortedOrders = sortedOrders - order; residualOrder
      case None => None
    }
  }

  /* Protected at package-level for testing; volatile for thread-safety. */
  @volatile protected[orderbooks] var sortedOrders = immutable.TreeSet.empty[A]

}


/** Factory for creating `ConcurrentSortedOrderBook` instances. */
object ConcurrentSortedOrderBook {

  /** Create a `ConcurrentSortedOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `ConcurrentSortedOrderBook` should be for the same `Tradable`.
    * @tparam A type of `Order` stored in the order book.
    */
  def apply[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A]): ConcurrentSortedOrderBook[A] = {
    new ConcurrentSortedOrderBook[A](tradable)(ordering)
  }

}