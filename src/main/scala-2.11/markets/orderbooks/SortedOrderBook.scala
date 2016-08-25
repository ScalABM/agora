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

import scala.collection.mutable


/** Class for modeling an `OrderBook` where the underlying collection of orders is sorted.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @param ordering an `Ordering` used to compare `Order` instances.
  * @tparam A the type of `Order` stored in the `SortedOrderBook`.
  */
class SortedOrderBook[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A])
  extends OrderBook[A](tradable) {

  /** Add an `Order` to the `SortedOrderBook`.
    *
    * @param order the `Order` that should be added to the `SortedOrderBook`.
    * @note Underlying implementation uses an `mutable.TreeSet` in order to guarantee that
    *       adding an `Order` to the `SortedOrderBook` is an `O(log n)` operation.
    */
  override def add(order: A): Unit = {
    super.add(order)
    sortedOrders.add(order)
  }
  
  /** Return the first `Order` in the `SortedOrderBook`.
    *
    * @return `None` if the `SortedOrderBook` is empty; `Some(order)` otherwise.
    * @note underlying implementation of `sortedOrders` guarantees that returning the first `Order` in the 
    *       `SortedOrderBook` is an `O(1)` operation.
    */
  def headOption: Option[A] = sortedOrders.headOption

  /** Remove and return the first `Order` in the `SortedOrderBook`.
    *
    * @return `None` if the `SortedOrderBook` is empty; `Some(order)` otherwise.
    * @note underlying implementation of `sortedOrders` guarantees that removing and returning the first `Order` in the
    *       `SortedOrderBook` is an `O(log n)` operation.
    */
  def remove(): Option[A] = {
    headOption match {
      case Some(order) => remove(order.uuid)
      case None => None
    }
  }
  
  /** Remove and return an existing `Order` from the `SortedOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `SortedOrderBook`.
    * @return `None` if the `uuid` is not found; `Some(order)` otherwise.
    * @note underlying implementation of `sortedOrders` uses an `mutable.TreeSet`; therefore
    *       removing an `Order` is an `O(log n)` operation.
    */
  override def remove(uuid: UUID): Option[A] = super.remove(uuid) match {
    case residualOrder @ Some(order) =>
      sortedOrders.remove(order); residualOrder
    case None => None
  }

  /* Protected at package-level for testing. */
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