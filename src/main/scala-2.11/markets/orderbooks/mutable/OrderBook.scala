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
package markets.orderbooks.mutable

import java.util.UUID

import markets.orderbooks.AbstractOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable


/** Class for modeling a simple `OrderBook`.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  */
class OrderBook[A <: Order](tradable: Tradable) extends AbstractOrderBook[A](tradable) {

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    */
  def add(order: A): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
  }

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[A] = {
    val residualOrder = existingOrders.get(uuid)
    existingOrders -= uuid; residualOrder
  }

  /* Protected at package-level for testing. */
  protected[orderbooks] val existingOrders = mutable.Map.empty[UUID, A]

}


/** Factory for creating `OrderBook` instances. */
object OrderBook {

  /** Create and `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam A type of `Order` stored in the order book.
    */
  def apply[A <: Order](tradable: Tradable): OrderBook[A] = new OrderBook[A](tradable)

}