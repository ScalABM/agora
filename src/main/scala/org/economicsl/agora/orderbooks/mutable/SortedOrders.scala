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
package org.economicsl.agora.orderbooks.mutable

import java.util.UUID

import org.economicsl.agora.generics
import org.economicsl.agora.tradables.orders.Order

import scala.collection.mutable



trait SortedOrders[O <: Order] extends generics.orderbooks.SortedOrders[O, mutable.TreeSet[O]] {
  this: OrderBook[O] with ExistingOrders[O] =>

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    * @note adding an `order` to the `OrderBook` is an `O(log n)` operation.
    */
  override def add(order: O): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
    sortedOrders.add(order)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies `p`; otherwise, some `Order`.
    */
  override def find(p: (O) => Boolean): Option[O] = sortedOrders.find(p)

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; otherwise, some `Order`.
    */
  override def headOption: Option[O] = sortedOrders.headOption

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found; otherwise, some `Order`.
    * @note removing and returning an existing `Order` from the `OrderBook` is an `O(log n)` operation.
    */
  override def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid) match {
    case residualOrder @ Some(order) => sortedOrders.remove(order); residualOrder
    case None => None
  }

}

