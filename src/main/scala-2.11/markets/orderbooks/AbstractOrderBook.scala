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
package markets.orderbooks

import java.util.UUID

import markets.orders.Order
import markets.tradables.Tradable


/** Abstract class defining the interface for an `OrderBook`.
  *
  * @param tradable all `Orders` contained in an `OrderBook` should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  */
abstract class AbstractOrderBook[A <: Order](val tradable: Tradable) {

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    */
  def add(order: A): Unit

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (A) => Boolean): Iterable[A] = {
    existingOrders.values.filter(p)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (A) => Boolean): Option[A] = {
    existingOrders.values.find(p)
  }

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[A] = existingOrders.values.headOption

  /** Remove and return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def remove(): Option[A] = {
    headOption match {
      case Some(order) => remove(order.uuid)
      case None => None
    }
  }

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the `OrderBook`; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[A]

  /* Underlying collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] def existingOrders: collection.Map[UUID, A]

}