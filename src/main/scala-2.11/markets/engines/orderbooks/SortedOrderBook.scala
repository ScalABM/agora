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
package markets.engines.orderbooks

import java.util.UUID

import markets.orders.Order
import markets.orders.limit.LimitOrder
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.{Failure, Success, Try}


/** Class for modeling an `OrderBook` where the underlying collection of orders is sorted.
  *
  * @param tradable All `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @param ordering
  * @tparam A type of `Order` stored in the order book.
  */
class SortedOrderBook[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A])
  extends OrderBook[A](tradable) {

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    * @return `Success(_)` if the `order` is added to the `OrderBook`; `Failure(ex)` otherwise.
    * @note Underlying implementation uses an `mutable.TreeSet` in order to guarantee that
    *       adding an `Order` is an `O(log n)` operation.
    */
  override def add(order: A): Try[Unit] = super.add(order) match {
    case Success(_) => Try(sortedExistingOrders += order)
    case failure @ Failure(ex) => failure
  }

  /** Remove and return the highest priority order in the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    */
  def pollPriorityOrder(): Option[A] = priorityOrder match {
    case Some(order) => remove(order.uuid)
    case None => None
  }

  /** Return the highest priority `LimitOrder` in the `SortedOrderBook`.
    *
    * @return `None` if the order book does not contain a `LimitOrder`; `Some(order)` otherwise.
    */
  def priorityLimitOrder: Option[A] = {
    sortedExistingOrders.find(order => order.isInstanceOf[LimitOrder])
  }

  /** Return the highest priority `Order` in the `SortedOrderBook`.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    */
  def priorityOrder: Option[A] = sortedExistingOrders.headOption

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note Underlying implementation uses a `mutable.TreeSet` in order to guarantee that
    *       removing an `Order` is an `O(log n)` operation.
    */
  override def remove(uuid: UUID): Option[A] = super.remove(uuid) match {
    case residualOrder @ Some(order) => sortedExistingOrders -= order; residualOrder
    case None => None
  }

  /* Protected at package-level for testing. */
  protected[orderbooks] val sortedExistingOrders = mutable.TreeSet.empty[A](ordering)

}


object SortedOrderBook {

  def apply[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A]): SortedOrderBook[A] = {
    new SortedOrderBook(tradable)(ordering)
  }

}