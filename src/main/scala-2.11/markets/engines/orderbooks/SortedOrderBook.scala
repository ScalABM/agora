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
import markets.orders.limit.LimitOrder

import scala.collection.generic.Sorted


/** Mixin trait for the OrderBook interface providing additional behavior for when sorted,
  * iterable collections are used as the underlying backing store.
  *
  * @tparam A type of `Order` stored in the order book.
  * @tparam CC type of sorted, iterable collection used as the backing store.
  * @note the covariant type annotation on `CC` implies that if some sorted, iterable collection
  *       `CC2` is a sub-type of another sorted, iterable collection `CC1`, then
  *       `SortedOrderBook[A,CC2]` is a sub-type of `SortedOrderBook[A,CC1]`.
  */
trait SortedOrderBook[A <: Order, +CC <: Iterable[A] with Sorted[A, CC]] {
  this: OrderBook[A, CC] =>

  /** An ordering defined over orders of type `A`. */
  def ordering: Ordering[A]

  /** Returns the best limit order in the order book.
    *
    * @return
    */
  def bestLimitOrder: Option[A] = {
    backingStore.find(order => order.isInstanceOf[LimitOrder])
  }

  /** Return the head of the order book.
    *
    * @return `None` if the order book is empty; else `Some(order)`.
    */
  def headOption: Option[A] = {
    backingStore.headOption
  }

  /** Remove and return the head of the order book.
    *
    * @return `None` if the order book is empty; else `Some(order)`.
    */
  def pop(): Option[A] = headOption match {
    case result @ Some(order) => remove(order); result
    case None => None
  }

}
