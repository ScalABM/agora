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

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[A]

  /* Protected at package-level for testing. */
  protected[orderbooks] def existingOrders: collection.Map[UUID, A]

}