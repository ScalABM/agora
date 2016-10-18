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

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable


/** Class for modeling an `OrderBook` where the underlying collection of orders is sorted.
  *
  * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
  * @param ordering an `Ordering` used to compare `Order` instances.
  * @param cbf
  * @tparam O the type of `Order` stored in the `SortedOrderBook`.
  * @tparam CC type of underlying collection class used to store the `Order` instances.
  */
class SortedOrderBook[O <: Order, +CC <: mutable.Map[UUID, O]](val tradable: Tradable)
                                                              (implicit ordering: Ordering[O], cbf: CanBuildFrom[_, _, CC])
  extends orderbooks.OrderBook[O, CC] with orderbooks.SortedOrders[O, CC, mutable.TreeSet[O]] {

  /** Add an `Order` to the `SortedOrderBook`.
    *
    * @param order the `Order` that should be added to the `SortedOrderBook`.
    * @note adding an `Order` to the `SortedOrderBook` is an `O(log n)` operation.
    */
  def add(order: O): Unit = {
    require(order.tradable == tradable)
    existingOrders += (order.uuid -> order)
    sortedOrders.add(order)
  }

  /** Filter the `SortedOrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (O) => Boolean): Option[Iterable[O]] = {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Find the first `Order` in the `SortedOrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `SortedOrderBook` satisfies the predicate; `Some(order)` otherwise.
    * @note `find` iterates over the `SortedOrderBook` in ascending order starting from the `head` `Order`.
    */
  def find(p: (O) => Boolean): Option[O] = {
    sortedOrders.find(p)
  }

  /** Return the head `Order` of the `SortedOrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    * @note the head `Order` of the `SortedOrderBook` is the head `Order` of the underlying `sortedOrders`.
    */
  def headOption: Option[O] = sortedOrders.headOption

  /** Reduces the existing orders of this `SortedOrderBook`, if any, using the specified associative binary operator.
    *
    * @param op an associative binary operator.
    * @return `None` if the `SortedOrderBook` is empty; the result of applying the `op` to the existing orders in the
    *         `SortedOrderBook` otherwise.
    * @note reducing the existing orders of a `SortedOrderBook` is an `O(n)` operation.
    */
  def reduce(op: (O, O) => O): Option[O] = sortedOrders.reduceOption(op)

  /** Remove and return the head `Order` of the `SortedOrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def remove(): Option[O] = headOption match {
    case Some(order) => remove(order.uuid)
    case None => None
  }

  /** Remove and return an existing `Order` from the `SortedOrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `SortedOrderBook`.
    * @return `None` if the `uuid` is not found; `Some(order)` otherwise.
    * @note removing and returning an existing `Order` from the `SortedOrderBook` is an `O(log n)` operation.
    */
  def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid) match {
    case residualOrder @ Some(order) =>
      sortedOrders.remove(order)  // this should always return true!
      residualOrder
    case None => None
  }

  /* Underlying collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] val existingOrders = cbf().result()

  /* Underlying sorted collection of `Order` instances; protected at package-level for testing. */
  protected[orderbooks] val sortedOrders = mutable.TreeSet.empty[O](ordering)

}


/** Companion object for `SortedOrderBook`.
  *
  * Used as a factory for creating `SortedOrderBook` instances.
  */
object SortedOrderBook {

  /** Create a `SortedOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
    * @param ordering an `Ordering` used to compare `Order` instances.
    * @param cbf
    * @tparam O type of `Order` stored in the order book.
    * @tparam CC type of underlying collection class used to store the `Order` instances.
    */
  def apply[O <: Order, CC <: mutable.Map[UUID, O]](tradable: Tradable)
                                                   (implicit ordering: Ordering[O], cbf: CanBuildFrom[_, _, CC]): SortedOrderBook[O, CC] =  {
    new SortedOrderBook[O, CC](tradable)(ordering, cbf)
  }

  /** Create an `SortedOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `SortedOrderBook` should be for the same `Tradable`.
    * @param ordering an `Ordering` used to compare `Order` instances.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable)(implicit ordering: Ordering[O]): SortedOrderBook[O, mutable.HashMap[UUID, O]] =  {
    val cbf = implicitly[CanBuildFrom[_,_,mutable.HashMap[UUID, O]]]
    new SortedOrderBook[O, mutable.HashMap[UUID, O]](tradable)(ordering, cbf)
  }

}