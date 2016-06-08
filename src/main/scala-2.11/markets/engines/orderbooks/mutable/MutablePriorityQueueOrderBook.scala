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

import java.util.UUID

import markets.engines.orderbooks.SortedOrderBook
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable


/** Class implementing the MutableOrderBook interface using a `mutable.PriorityQueue` as the
  * underlying backing store.
  *
  * @param ordering an ordering defined over orders of type `A`.
  * @param tradable all orders stored in the order book should be for the same `Tradable`.
  * @tparam A type of `Order` stored in the order book.
  */
class MutablePriorityQueueOrderBook[A <: Order](ordering: Ordering[A], tradable: Tradable)
  extends SortedOrderBook[A, mutable.PriorityQueue[A]](ordering, tradable) {

  /** Add an order to the order book.
    *
    * @param order the order that should be added to the order book.
    * @note adding an order to the order book is a O(log n) operation.
    */
  override def add(order: A): Unit = {
    require(order.tradable == tradable) // validates order!
    backingStore.enqueue(order)
  }

  /** Remove and return the head of the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    * @note removal of the head order of the order book is an O(log n) operation.
    */
  override def pop(): Option[A] = if (backingStore.isEmpty) None else Some(backingStore.dequeue())

  /** Remove an order from the order book.
    *
    * @return `None` if the order is not in the order book; `Some(order)` otherwise.
    * @note removing an order from the order book is an O(n) operation.
    */
  def remove(order: A): Unit = {
    require(order.tradable == tradable)
    backingStore = backingStore.filterNot(o => o.uuid == order.uuid)
  }

  /** The backing store is implemented using a `mutable.PriorityQueue`. */
  protected var backingStore = mutable.PriorityQueue.empty[A](ordering)

}


object MutablePriorityQueueOrderBook {

  def apply[A <: Order](ordering: Ordering[A],
                        tradable: Tradable): MutablePriorityQueueOrderBook[A] = {
    new MutablePriorityQueueOrderBook[A](ordering, tradable)
  }

}