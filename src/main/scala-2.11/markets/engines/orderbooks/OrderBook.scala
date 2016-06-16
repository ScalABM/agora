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
import markets.tradables.Tradable

import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}


/** Class for modeling an `OrderBook`.
  *
  * @param tradable All `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  */
class OrderBook[A <: Order](val tradable: Tradable) {

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    * @return `Success(_)` if the `order` is added to the `OrderBook`; `Failure(ex)` otherwise.
    * @note Underlying implementation of uses an `immutable.HashMap` in order to guarantee that
    *       adding an `Order` to the `OrderBook` is an `O(1)` operation.
    */
  def add(order: A): Try[Unit] = {
    Try(require(order.tradable == tradable)) match {
      case Success(_) => Try(existingOrders += (order.uuid -> order))
      case failure @ Failure(ex) => failure
    }
  }

  /** Filter the `OrderBook` and return those orders that satisfy the given predicate.
    *
    * @param p predicate defining desirable characteristics of orders.
    * @return collection of orders satisfying the given predicate.
    */
  def filter(p: (A) => Boolean): Iterable[A] = {
    existingOrders.values.filter(p)
  }

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note Underlying implementation of uses an `immutable.HashMap` in order to guarantee that
    *       removing an `Order` from the `OrderBook` is an `O(1)` operation.
    */
  def remove(uuid: UUID): Option[A] = {
    val residualOrder = existingOrders.get(uuid)
    existingOrders -= uuid
    residualOrder
  }

  /* Protected at package-level for testing; volatile in order to guarantee thread-safety. */
  @volatile protected[orderbooks] var existingOrders = HashMap.empty[UUID, A]

}


object OrderBook {

  def apply[A <: Order](tradable: Tradable): OrderBook[A] = new OrderBook[A](tradable)

}