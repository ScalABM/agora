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
package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.OrderBook
import markets.orders.Order

import scala.collection.immutable.TreeSet


/** Mixin trait providing a `immutable.TreeSet` implementation for an order book.
  *
  * @tparam A the type of orders stored in the order book.
  */
trait ImmutableTreeSetLike[A <: Order] {
  this: OrderBook[A, TreeSet[A]] =>

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    */
  def add(order: A): Unit = {
    backingStore = backingStore + order
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    */
  def remove(order: A): Unit = {
    backingStore = backingStore - order
  }

  protected var backingStore: TreeSet[A]

}
