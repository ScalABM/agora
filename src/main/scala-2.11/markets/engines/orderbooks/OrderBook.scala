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

import scala.util.Try


/** Trait defining the `OrderBook` interface.
  *
  * @tparam A type of `Order` stored in the order book.
  */
trait OrderBook[A <: Order] {

  /** All `Order` instances contained in the `OrderBook` should be for the same `Tradable`. */
  def tradable: Tradable

  /** Add an order to the `OrderBook`.
    *
    * @param order the order that should be added to the `OrderBook`.
    */
  def add(order: A): Try[Unit] = {
    Try(require(order.tradable == tradable))  // validates the order!
  }

  /** Filter `existingOrders` and return those orders the satisfy the given predicate.
    *
    * @param p a predicate defining desirable characteristics of orders.
    * @return an iterable collection of orders that satisfy the given predicate.
    */
  def filter(p: (A) => Boolean): Iterable[A] = {
    existingOrders.values.filter(p)
  }

  /** Remove and return an order from the `OrderBook`.
    *
    * @param uuid the UUID for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[A]

  /* Protected at the package level to simplify testing. */
  protected[orderbooks] def existingOrders: Map[UUID, A]

}
