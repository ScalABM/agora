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
package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.SortedOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.immutable


/** Class implementing the `SortedOrderBook` interface using an `immutable.TreeSet` as the
  * underlying immutable backing store.
  *
  * @param ordering an ordering defined over orders of type `A`.
  * @tparam A type of `Order` stored in the order book.
  */
class ImmutableTreeSetOrderBook[A <: Order](ordering: Ordering[A], tradable: Tradable)
  extends SortedOrderBook[A, immutable.TreeSet[A]](ordering, tradable) {

  /** Add an order to the order book.
    *
    * @param order the order that should be added to the order book.
    * @note adding an order to the order book is an O(log n) operation.
    */
  override def add(order: A): Unit = {
    require(order.tradable == tradable) // validates order!
    backingStore = backingStore + order
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
    if (backingStore.contains(order)) { remove(order); Some(order) } else super.pop(order)
  }

  /** Remove an order from the order book.
    *
    * @param order the order that should be removed from the order book.
    * @note removing an order from the order book an O(log n) operation.
    */
  def remove(order: A): Unit = {
    require(order.tradable == tradable)  // validates order!
    backingStore = backingStore - order
  }

  /** Class uses an `immutable.TreeSet` as the underlying backing store. */
  protected var backingStore = immutable.TreeSet.empty[A](ordering)

}


object ImmutableTreeSetOrderBook {

  def apply[A <: Order](ordering: Ordering[A], tradable: Tradable): ImmutableTreeSetOrderBook[A] = {
    new ImmutableTreeSetOrderBook(ordering, tradable)
  }

}