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
package markets.engines.orderbooks.mutable

import markets.engines.orderbooks.SortedOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Try


/** Class implementing the `SortedOrderBook` interface using a `mutable.TreeSet` as the underlying
  * backing store.
  *
  * @param ordering an ordering defined over orders of type `A`.
  * @param tradable all orders stored in the order book should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  */
class MutableTreeSetOrderBook[A <: Order](ordering: Ordering[A], tradable: Tradable)
  extends SortedOrderBook[A, mutable.TreeSet[A]](ordering, tradable) {

  /** Add an order to the order book.
    *
    * @param order the order that should be added to the order book.
    * @note adding an order to the order book is a O(log n) operation.
    */
  override def add(order: A): Unit = {
    require(order.tradable == tradable) // validates order!
    backingStore.add(order)
  }

  /** Remove and return the head of the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    * @note removal of the head order of the order book is an O(log n) operation.
    */
  override def pop(): Option[A] = super.pop()

  /** Remove and return an order from the order book.
    *
    * @param order the order that should be added to the order book.
    * @return `None` if the order is not in the order book; `Some(order)` otherwise.
    * @note removal of an order from the order book is an O(n) operation.
    */
  override def pop(order: A): Option[A] = {
    require(order.tradable == tradable)
    if (backingStore.remove(order)) Some(order) else super.pop(order)
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    * @note removal of an order from the order book is an O(log n) operation.
    */
  def remove(order: A): Unit = {
    require(order.tradable == tradable) // validates order!
    backingStore.remove(order)
  }

  /** The underlying backing store is implemented using a `mutable.TreeSet`. */
  protected val backingStore = mutable.TreeSet.empty[A](ordering)

}


object MutableTreeSetOrderBook {

  def apply[A <: Order](ordering: Ordering[A], tradable: Tradable): MutableTreeSetOrderBook[A] = {
    new MutableTreeSetOrderBook(ordering, tradable)
  }

}


