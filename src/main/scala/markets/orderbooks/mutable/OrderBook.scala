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
package markets.orderbooks.mutable

import java.util.UUID

import markets.orderbooks
import markets.tradables.orders.Order
import markets.tradables.Tradable

import scala.collection.mutable


/** Trait defining the interface for a `mutable.OrderBook`. */
trait OrderBook[O <: Order, +CC <: mutable.Map[UUID, O]] extends orderbooks.OrderBook[O, CC] {

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    */
  def add(order: O): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
  }

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (O) => Boolean): Option[Iterable[O]] = {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (O) => Boolean): Option[O] = existingOrders.values.find(p)

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[O] = existingOrders.values.headOption

  /** Reduces the existing orders of this `OrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `OrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `OrderBook` otherwise.
    * @note reducing the existing orders of an `OrderBook` is an `O(n)` operation.
    */
  def reduce(op: (O, O) => O): Option[O] = existingOrders.values.reduceOption(op)

  /** Remove and return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def remove(): Option[O] = headOption match {
    case Some(order) => remove(order.uuid)
    case None => None
  }

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid)

}


/** Companion object for the `OrderBook` trait.
  *
  * Used as a factory for creating `OrderBook` instances.
  */
object OrderBook {

  /** Create an `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable): OrderBook[O, mutable.Map[UUID, O]] =  {
    DefaultOrderBook[O](tradable)
  }

  /** Create an `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](initialOrders: Iterable[O], tradable: Tradable): OrderBook[O, mutable.Map[UUID, O]] = {
    val orderBook = DefaultOrderBook[O](tradable)
    initialOrders.foreach(order => orderBook.add(order))
    orderBook
  }

  private[this] case class DefaultOrderBook[O <: Order](tradable: Tradable) extends OrderBook[O, mutable.Map[UUID, O]] {

    /* Underlying collection of `Order` instances. */
    protected val existingOrders = mutable.Map.empty[UUID, O]

  }

}