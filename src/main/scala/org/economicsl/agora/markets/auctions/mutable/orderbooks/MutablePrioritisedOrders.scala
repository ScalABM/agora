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

import org.economicsl.agora.markets.tradables.orders.Order

import scala.collection.mutable


/** Abstract class defining the interface for a `PriorityOrderBook`.
  *
  * @tparam O the type of `Order` stored in a `PriorityOrderBook`.
  */
trait MutablePrioritisedOrders[O <: Order] {
  this: OrderBook[O] with MutableExistingOrders[O] =>

  /** Add an `Order` to the `PriorityOrderBook`.
    *
    * @param order the `Order` that should be added to the `PriorityOrderBook`.
    * @note adding an `Order` to the `PriorityOrderBook` is an `O(1)` operation.
    */
  override def add(order: O): Unit = {
    this.add(order); prioritisedOrders.enqueue(order)
  }

  /** Return the highest priority `Order` in the `PriorityOrderBook`.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    * @note returning the highest priority `Order` from the `PriorityOrderBook` is an `O(1)` operation.
    */
  override def headOption: Option[O] = prioritisedOrders.headOption

  /** Remove and return the highest priority order in the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    * @note removing the highest priority `Order` from the `PriorityOrderBook` is an `O(log n)` operation.
    */
  def remove(): Option[O] = {
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
  override def find(p: (O) => Boolean): Option[O] = {
    prioritisedOrders.clone.dequeueAll.find(p)
  }

  /** Remove and return an existing `Order` from the `PriorityOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `PriorityOrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note removing and returning an `Order` from the `PriorityOrderBook` is an `O(n)` operation.
    */
  def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid) match {
    case residualOrder @ Some(order) =>
      prioritisedOrders = prioritisedOrders.filterNot(order => order.uuid == uuid)
      residualOrder
    case None => None
  }
  /* Underlying prioritised collection of `Order` instances. */
  protected[orderbooks] def prioritisedOrders: mutable.PriorityQueue[O]

}

