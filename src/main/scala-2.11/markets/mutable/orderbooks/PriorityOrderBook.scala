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

import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable


/** Class for modeling an `PriorityOrderBook` where the underlying collection of orders is prioritised.
  *
  * @param tradable all `Order` instances contained in the `PriorityOrderBook` should be for the same `Tradable`.
  * @param ordering an `Ordering` used to compare `Order` instances.
  * @tparam A type of `Order` stored in the order book.
  */
class PriorityOrderBook[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A])
  extends AbstractPriorityOrderBook[A](tradable) {
  
  /** Add an `Order` to the `PriorityOrderBook`.
    *
    * @param order the `Order` that should be added to the `PriorityOrderBook`.
    * @note adding an `Order` to the `PriorityOrderBook` is an `O(1)` operation.
    */
  def add(order: A): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
    prioritisedOrders.enqueue(order)
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

  /** Return the highest priority `Order` in the `PriorityOrderBook`.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    * @note returning the highest priority `Order` from the `PriorityOrderBook` is an `O(1)` operation.
    */
  override def headOption: Option[A] = prioritisedOrders.headOption

  /** Remove and return the highest priority order in the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    * @note removing the highest priority `Order` from the `PriorityOrderBook` is an `O(log n)` operation.
    */
  override def remove(): Option[A] = {
    if (prioritisedOrders.isEmpty) {
      assert(existingOrders.isEmpty)  // should never happen!
      None
    } else {
      val head = prioritisedOrders.dequeue()
      existingOrders.remove(head.uuid)
    }
  }

  /** Find the first `Order` in the `PriorityOrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `PriorityOrderBook` satisfies the predicate; `Some(order)` otherwise.
    * @note `find` iterates over the `PriorityOrderBook` in priority order starting from the `head` `Order`.
    */
  override def find(p: (A) => Boolean): Option[A] = {
    prioritisedOrders.clone.dequeueAll.find(p)
  }

  /** Remove and return an existing `Order` from the `PriorityOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `PriorityOrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note removing and returning an `Order` from the `PriorityOrderBook` is an `O(n)` operation.
    */
  def remove(uuid: UUID): Option[A] = existingOrders.remove(uuid) match {
    case residualOrder @ Some(order) =>
      prioritisedOrders = prioritisedOrders.filterNot(order => order.uuid == uuid)
      residualOrder
    case None => None
  }

  /* Underlying collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] val existingOrders = mutable.HashMap.empty[UUID, A]

  /* Underlying collection of prioritised `Order` instances; protected at package-level for testing. */
  protected[orderbooks] var prioritisedOrders = mutable.PriorityQueue.empty[A](ordering)

}


/** Factory for creating `PriorityOrderBook` instances. */
object PriorityOrderBook {

  /** Create a `PriorityOrderBook` for a particular `Tradable`.
    *
    * @param tradable All `Orders` contained in the `PriorityOrderBook` should be for the same `Tradable`.
    * @param ordering an `Ordering` used to compare `Order` instances.
    * @tparam A type of `Order` stored in the order book.
    */
  def apply[A <: Order](tradable: Tradable)(implicit ordering: Ordering[A]): PriorityOrderBook[A] = {
    new PriorityOrderBook(tradable)(ordering)
  }

}