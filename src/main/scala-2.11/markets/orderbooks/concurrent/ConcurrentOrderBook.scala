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
package markets.orderbooks.concurrent

import java.util.UUID

import markets.orderbooks.AbstractOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.immutable


/** Class for modeling an `OrderBook` for use when thread-safe access is required.
  *
  * @param tradable all `Orders` contained in the `ConcurrentOrderBook` should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the `ConcurrentOrderBook`.
  */
class ConcurrentOrderBook[A <: Order](tradable: Tradable) extends AbstractOrderBook[A](tradable) {

  /** Add an `Order` to the `ConcurrentOrderBook`.
    *
    * @param order the `Order` that should be added to the `ConcurrentOrderBook`.
    * @note underlying implementation guarantees that adding an `Order` to the `ConcurrentOrderBook` is an `O(1)`
    *       operation.
    */
  override def add(order: A): Unit = {
    require(order.tradable == tradable)
    existingOrders = existingOrders + (order.uuid -> order)
  }

  /** Remove and return an existing `Order` from the `ConcurrentOrderBook`.
    *
    * @param uuid the `UUID` for the `Order` that should be removed from the `ConcurrentOrderBook`.
    * @return `None` if the `uuid` is not found in the `ConcurrentOrderBook`; `Some(order)` otherwise.
    * @note underlying implementation guarantees that removing and returning an `Order` from the `ConcurrentOrderBook`
    *       is an `O(1)` operation.
    */
  def remove(uuid: UUID): Option[A] = {
    existingOrders.get(uuid) match {
      case residualOrder @ Some(order) =>
        existingOrders = existingOrders - uuid; residualOrder
      case None => None
    }
  }

  /* Protected at package-level for testing; volatile for thread-safety. */
  @volatile protected[orderbooks] var existingOrders = immutable.Map.empty[UUID, A]

}


/** Factory for creating `ConcurrentOrderBook` instances. */
object ConcurrentOrderBook {

  /** Create a `ConcurrentOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `ConcurrentOrderBook` should be for the same `Tradable`.
    * @tparam A type of `Order` stored in the `ConcurrentOrderBook`.
    */
  def apply[A <: Order](tradable: Tradable): ConcurrentOrderBook[A] = new ConcurrentOrderBook[A](tradable)

}