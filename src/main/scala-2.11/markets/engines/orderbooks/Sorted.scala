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
package markets.engines.orderbooks

import markets.orders.Order

import scala.collection.SortedSet
import scala.util.{Success, Try}


/** Mixin trait defining the interface of a `SortedOrderBook`.
  *
  * @tparam A type of `Order` stored in the `SortedOrderBook`.
  */
trait Sorted[A <: Order] {
  this: OrderBook[A] =>

  implicit val ordering: Ordering[A]

  /** View the highest priority order in the `OrderBook`.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    */
  def headOption: Option[A] = sortedExistingOrders.headOption

  /** Remove and return the highest priority order in the order book.
    *
    * @return `None` if the order book is empty; `Some(order)` otherwise.
    */
  def poll(): Option[A] = headOption match {
    case priorityOrder @ Some(order) => remove(order.uuid); priorityOrder
    case None => None
  }

  /* Protected at the package level to simplify testing. */
  protected[orderbooks] def sortedExistingOrders: SortedSet[A]

}
