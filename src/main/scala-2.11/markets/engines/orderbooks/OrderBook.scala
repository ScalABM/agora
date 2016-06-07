/*
Copyright 2016 David R. Pugh

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

import markets.orders.Order


/** Abstract class defining the interface for all order books.
  *
  * @tparam A type of `Order` stored in the order book.
  * @tparam CC type of iterable collection used as the backing store.
  * @note the covariant type annotation on `CC` implies that if some iterable collection `CC2`
  *       is a sub-type of another iterable collection `CC1`, then `OrderBook[A,CC2]` is a
  *       sub-type of `OrderBook[A,CC1]`.
  */
abstract class OrderBook[A <: Order, +CC <: Iterable[A]] {

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    * @note adding an order is a side effect.
    */
  def add(order: A): Unit

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    * @return true if the order is removed; false otherwise.
    * @note removing an order is a side effect.
    */
  def remove(order: A): Boolean

  /** The underlying iterable collection which stores all `Orders`. */
  protected def backingStore: CC

}
