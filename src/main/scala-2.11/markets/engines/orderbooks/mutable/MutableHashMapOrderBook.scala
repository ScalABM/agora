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

import java.util.UUID

import markets.orders.Order

import scala.collection.mutable


/** Class implementing the `MutableOrderBook` interface using a `mutable.HashMap` as the underlying
  * backing store.
  *
  * @tparam A type of `Order` stored in the order book.
  */
class MutableHashMapOrderBook[A <: Order] extends MutableOrderBook[A, mutable.HashMap[UUID, A]] {

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    * @note adding an order is an O(1) operation.
    */
  def add(order: A): Unit = {
    backingStore += (order.uuid -> order)
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    * @note removing an order is an O(1) operation.
    */
  def remove(order: A): Boolean = {
    if (backingStore.contains(order.uuid)) {
      backingStore -= order.uuid
      true
    } else {
      false
    }
  }

  protected def backingStore = mutable.HashMap.empty[UUID, A]

}


/** Companion object for MutableHashMapOrderBook. */
object MutableHashMapOrderBook {

  def apply[A <: Order](): MutableHashMapOrderBook[A] = new MutableHashMapOrderBook[A]()

}

