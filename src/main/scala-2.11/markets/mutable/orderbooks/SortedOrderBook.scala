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
package markets.mutable.orderbooks

import java.util.UUID

import markets.generic.AbstractSortedOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable


/** Class for modeling an `OrderBook` where the underlying collection of orders is sorted.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @param ordering an `Ordering` used to compare `Order` instances.
  * @tparam A the type of `Order` stored in the `SortedOrderBook`.
  */
class SortedOrderBook[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A])
  extends AbstractSortedOrderBook[A](tradable) {

  /** Add an `Order` to the `SortedOrderBook`.
    *
    * @param order the `Order` that should be added to the `SortedOrderBook`.
    * @note adding an `Order` to the `SortedOrderBook` is an `O(log n)` operation.
    */
  def add(order: A): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
    sortedOrders.add(order)
  }

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (A) => Boolean): Option[Iterable[A]] = {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Remove and return an existing `Order` from the `SortedOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `SortedOrderBook`.
    * @return `None` if the `uuid` is not found; `Some(order)` otherwise.
    * @note removing and returning an existing `Order` from the `SortedOrderBook` is an `O(log n)` operation.
    */
  def remove(uuid: UUID): Option[A] = existingOrders.remove(uuid) match {
    case residualOrder @ Some(order) =>
      sortedOrders.remove(order)  // this should always return true!
      residualOrder
    case None => None
  }

  /* Underlying collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] val existingOrders = mutable.HashMap.empty[UUID, A]

  /* Underlying sorted collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] val sortedOrders = mutable.TreeSet.empty[A](ordering)

}


/** Factory for creating `SortedOrderBook` instances. */
object SortedOrderBook {

  /** Create a `SortedOrderBook` for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @param ordering an `Ordering` used to compare `Order` instances.
    * @tparam A the type of `Order` stored in the `SortedOrderBook`.
    */
  def apply[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A]): SortedOrderBook[A] = {
    new SortedOrderBook(tradable)(ordering)
  }

}