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

import markets.orders.Order
import markets.orders.limit.LimitOrder
import markets.tradables.Tradable

import scala.collection.generic.Sorted


/** Abstract class defining the interface for a `SortedOrderBook`.
  *
  * @param ordering an ordering defined over orders of type `A`.
  * @param tradable all orders stored in the order book should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  * @tparam CC type of sorted, iterable collection used as the backing store.
  * @note the covariant type annotation on `CC` implies that if some sorted, iterable collection
  *       `CC2` is a sub-type of another sorted, iterable collection `CC1`, then
  *       `SortedOrderBook[A, CC2]` is a sub-type of `SortedOrderBook[A, CC1]`.
  */
abstract class SortedOrderBook[A <: Order, +CC <: Iterable[A]](val ordering: Ordering[A],
                                                               tradable: Tradable)
  extends OrderBook[A, CC](tradable) {

  /** Returns the best limit order in the order book.
    *
    * @return `None` if the order book has no limit orders; `Some(order)` otherwise.
    */
  def bestLimitOrder: Option[A] = {
    backingStore.find(order => order.isInstanceOf[LimitOrder])
  }

  /** Return the head of the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    */
  def headOption: Option[A] = {
    backingStore.headOption
  }

  /** Remove and return the head of the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    */
  def pop(): Option[A] = headOption match {
    case result @ Some(order) => remove(order); result
    case None => None
  }

}
