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

import markets.engines.orderbooks.SortedOrderBook
import markets.orders.Order

import scala.collection.immutable


/** Class implementing the ImmutableOrderBook interface using an immutable TreeSet as the
  * underlying immutable backing store.
  *
  * @param ordering an ordering defined over orders of type `A`.
  * @tparam A type of `Order` stored in the order book.
  */
class ImmutableTreeSetOrderBook[A <: Order](val ordering: Ordering[A])
  extends ImmutableOrderBook[A, immutable.TreeSet[A]]
  with SortedOrderBook[A, immutable.TreeSet[A]] {

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    * @note adding an order is an O(log n) operation.
    */
  def add(order: A): Unit = {
    backingStore = backingStore + order
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    * @return true if the order is removed; false otherwise.
    * @note removing an order is an O(log n) operation.
    */
  def remove(order: A): Boolean = {
    backingStore = backingStore - order
  }

  /** Class uses an `immutable.TreeSet` as the underlying backing store. */
  protected var backingStore = immutable.TreeSet.empty[A](ordering)

}
