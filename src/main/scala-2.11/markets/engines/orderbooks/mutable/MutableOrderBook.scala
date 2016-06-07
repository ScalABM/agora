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
package markets.engines.orderbooks.mutable

import markets.engines.orderbooks.OrderBook
import markets.orders.Order

import scala.collection.mutable


/** Abstract class defining the interface for all order books whose underlying backing store is a
  * mutable, iterable collection.
  *
  * @tparam A type of `Order` stored in the order book.
  * @tparam CC type of mutable, iterable collection used as the backing store.
  * @note the covariant type annotation on `CC` implies that if some mutable, iterable collection
  *       `CC2` is a sub-type of another mutable, iterable collection `CC1`, then
  *       `MutableOrderBook[A,CC2]` is a sub-type of `MutableOrderBook[A,CC1]`.
  */
abstract class MutableOrderBook[A <: Order, +CC <: mutable.Iterable[A]] extends OrderBook[A, CC]


