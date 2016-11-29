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

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.mutable


/** Mixin trait defining the interface for a `SortedOrderBook`.
  *
  * @tparam O the type of `Order` stored in a `SortedOrderBook`.
  */
trait SortedOrders[O <: Order with Persistent] {
  this: OrderBookLike[O] with ExistingOrders[O] =>

  /** Add an new `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    * @note Following the design of the University of Michigan Auction Bot, an `issuer` can have only one active order
    *       at a time: a new order added to the `OrderBook` supersedes any existing order with the same issuer. This
    *       requirement is not a restriction per se as the general definition of an `Order` allows an agent to
    *       potentially specify any arbitrary function.
    */
  override def add(order: O): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.issuer -> order); sortedOrders.add(order)
  }

  /** Remove all existing `Order` instances from the `OrderBook`. */
  override def clear(): Unit = {
    existingOrders.clear(); sortedOrders.clear()
  }

  /** Find the first `Order` in the `SortedOrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `SortedOrderBook` satisfies the predicate; `Some(order)` otherwise.
    * @note `find` iterates over the `SortedOrderBook` in ascending order starting from the `head` `Order`.
    */
  override def find(p: (O) => Boolean): Option[O] = sortedOrders.find(p)

  /** Return the head `Order` of the `SortedOrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    * @note the head `Order` of the `SortedOrderBook` is the head `Order` of the underlying `sortedOrders`.
    */
  override def headOption: Option[O] = sortedOrders.headOption

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` does not contain any `Order` instances; `false`, otherwise.
    */
  override def isEmpty: Boolean = existingOrders.isEmpty && sortedOrders.isEmpty

  /** Boolean flag indicating whether or not the `OrderBook` contains `Order` instances.
    *
    * @return `true`, if the `OrderBook` contains any `Order` instances; `false`, otherwise.
    */
  override def nonEmpty: Boolean = existingOrders.nonEmpty && sortedOrders.nonEmpty

  /** Remove and return an existing `Order` from the `SortedOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `SortedOrderBook`.
    * @return `None` if the `uuid` is not found; `Some(order)` otherwise.
    * @note removing and returning an existing `Order` from the `SortedOrderBook` is an `O(log n)` operation.
    */
  override def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid) match {
    case residualOrder @ Some(order) =>
      sortedOrders.remove(order)  // this should always return true!
      residualOrder
    case None => None
  }

  protected[orderbooks] def sortedOrders: mutable.TreeSet[O]

}

