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


/** Mixin trait providing a `mutable.TreeSet` implementation for an order book.
  *
  * @tparam A the type of orders stored in the order book.
  */
trait MutableSetLike[A <: Order] {
  this: OrderBook[A, mutable.Set[A]] =>

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    */
  def add(order: A): Unit = {
    backingStore += order
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    */
  def remove(order: A): Unit = {
    backingStore -= order
  }

  protected val backingStore: mutable.Set[A]

}
