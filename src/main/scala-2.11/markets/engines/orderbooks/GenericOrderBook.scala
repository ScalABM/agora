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


/** Base trait for all order books.
  *
  * An order book is a collection of orders (typically either ask or bid orders).
  * @tparam A the type of orders stored in the order book.
  * @tparam B the type of underlying collection used to store the orders.
  */
trait GenericOrderBook[A <: Order, +B <: Iterable[A]] {

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    */
  def add(order: A): Unit

  /** Returns the best limit order in the order book.
    *
    * @return
    */
  def bestLimitOrder: Option[A] = {
    backingStore.find(order => order.isInstanceOf[LimitOrder])
  }

  /** Returns the head of the order book.
    *
    * @return if the order book is empty, `None`; else `Some(order)`.
    */
  def headOption: Option[A] = {
    backingStore.headOption
  }

  /** Remove and return the head of the order book.
    *
    * @return if the order book is empty, `None`; else `Some(order)`.
    * @note Removal of the head order is a side effect.
    */
  def pop(): Option[A] = {
    val result = headOption
    result match {
      case Some(order) => remove(order)  // SIDE EFFECT!
      case None =>  // Nothing to remove!
    }
    result
  }

  /** Remove and return a specific order from the order book.
    *
    * @return if the order can not be found in the order book, `None`; else `Some(residualOrder)`.
    * @note Removal of the order is a side effect.
    */
  def pop(order: A): Option[A] = {
    val result = backingStore.find(o => o.uuid == order.uuid)
    result match {
      case Some(residualOrder) => remove(residualOrder) // SIDE EFFECT!
      case None =>  // nothing to remove!
    }
    result
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    */
  def remove(order: A): Unit

  /* The underlying backing store containing the orders. */
  protected def backingStore: B


}
