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
  * @param tradable all orders stored in the order book should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  * @tparam CC type of iterable collection used as the backing store.
  * @note the covariant type annotation on `CC` implies that if some iterable collection `CC2`
  *       is a sub-type of another iterable collection `CC1`, then `OrderBook[A, CC2]` is a
  *       sub-type of `OrderBook[A, CC1]`.
  */
abstract class OrderBook[A <: Order, +CC <: Iterable[A]](val tradable: Tradable) {

  /** Add an order to the order book.
    *
    * @param order the order that should be added to the order book.
    */
  def add(order: A): Unit

  /** Remove and return an order from the order book.
    *
    * @param order the order that should be added to the order book.
    * @return `None` if the order is not in the order book; `Some(order)` otherwise.
    * @note removal of an order from the order book is an O(n) operation.
    */
  def pop(order: A): Option[A] = {
    require(order.tradable == tradable)
    lookUpBy(order.uuid) match {
      case result @ Some(existingOrder) => remove(existingOrder); result
      case None => None
    }
  }

  /** Remove an order from the order book.
    *
    * @param order the order that should be removed from the order book.
    */
  def remove(order: A): Unit

  /** The underlying iterable collection which stores all `Orders`. */
  protected def backingStore: CC

  /* Used to find an order by its uuid so that it can be removed from the `backingStore`. */
  private[this] def lookUpBy(uuid: UUID) = backingStore.find(order => order.uuid == uuid)

}
