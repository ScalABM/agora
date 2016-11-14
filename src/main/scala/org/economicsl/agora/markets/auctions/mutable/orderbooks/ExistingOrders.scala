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

import org.economicsl.agora.markets.auctions
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import scala.collection.mutable


/** Mixin trait defining the interface for an `OrderBookLike` containing existing orders.
  *
  * @tparam O the type of `Order` stored in a `OrderBook`.
  */
trait ExistingOrders[O <: Order with Persistent] extends auctions.orderbooks.ExistingOrders[O, mutable.Map[UUID, O]] {
  this: auctions.orderbooks.OrderBookLike[O] =>

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    */
  def add(order: O): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
  }

  /** Remove and return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def remove(): Option[O] = headOption.flatMap(order => remove(order.uuid))

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid)

}

