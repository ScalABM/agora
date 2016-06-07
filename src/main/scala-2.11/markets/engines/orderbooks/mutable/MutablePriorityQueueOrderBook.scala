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

import markets.engines.orderbooks.SortedOrderBook
import markets.orders.Order

import scala.collection.mutable


/** Class implementing the MutableOrderBook interface using a `PriorityQueue` as the underlying
  * mutable backing store.
  *
  * @param ordering an ordering defined over orders of type `A`.
  * @tparam A type of `Order` stored in the order book.
  */
class MutablePriorityQueueOrderBook[A <: Order](val ordering: Ordering[A])
  extends MutableOrderBook[A, mutable.PriorityQueue[A]]
  with SortedOrderBook[A, mutable.PriorityQueue[A]]{

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    */
  def add(order: A): Unit = backingStore.enqueue(order)

  /** Remove and return the head of the order book.
    *
    * @return if the order book is empty, `None`; else `Some(order)`.
    * @note removal of the head order is a side effect.
    */
  override def pop(): Option[A] = if (backingStore.isEmpty) None else Some(backingStore.dequeue())

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    * @return true if the order is removed; false otherwise.
    * @note removing an order is a side effect.
    */
  def remove(order: A): Boolean = {
    val filteredBackingStore = backingStore.filterNot( o => o.uuid == order.uuid )
    if (filteredBackingStore.size == backingStore.size) {
      false
    } else {
      backingStore = filteredBackingStore
      true
    }
  }

  protected var backingStore = mutable.PriorityQueue.empty[A](ordering)

}


object MutablePriorityQueueOrderBook {

  def apply[A <: Order](ordering: Ordering[A]): MutablePriorityQueueOrderBook[A] = {
    new MutablePriorityQueueOrderBook[A](ordering)
  }

}