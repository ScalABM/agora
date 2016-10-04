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
package markets.parallel.mutable.orderbooks

import java.util.UUID

import markets.generic
import markets.tradables.orders.Order
import markets.tradables.Tradable

import scala.collection.generic.CanBuildFrom
import scala.collection.parallel
import scala.concurrent.forkjoin.ForkJoinPool


/** Class for modeling an `OrderBook`.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam O type of `Order` stored in the order book.
  * @tparam CC type of underlying collection class used to store the `Order` instances.
  */
class OrderBook[O <: Order, +CC <: parallel.mutable.ParMap[UUID, O]](val tradable: Tradable)(implicit cbf: CanBuildFrom[_, _, CC], ts: parallel.TaskSupport)
  extends generic.OrderBook[O, CC] {

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
  def filter(p: (O) => Boolean): Option[parallel.ParIterable[O]] = {
    val filteredOrders = existingOrders.filter { case (_, order) => p(order) }
    if (filteredOrders.isEmpty) None else Some(filteredOrders.values)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (O) => Boolean): Option[O] = {
    existingOrders.find { case (_, order) => p(order) } match {
      case Some((_, order)) => Some(order)
      case None => None
    }
  }

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[O] = existingOrders.headOption match {
    case Some((_, order)) => Some(order)
    case None => None
  }

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
  def remove(uuid: UUID): Option[O] = {
    val removedOrder = existingOrders.get(uuid); existingOrders -= uuid
    removedOrder

  }

  /* Protected at package-level for testing. */
  protected[orderbooks] val existingOrders: CC = {
    val initialOrders = cbf().result()
    initialOrders.tasksupport = ts  // allows user to customize amount of parallelism!
    initialOrders
  }

}


/** Companion object for `OrderBook`.
  *
  * Used as a factory for creating `OrderBook` instances.
  */
object OrderBook {

  /** Default `TaskSupport` is a `ForkJoinPool` where number of threads equals the number of available processors. */
  implicit val taskSupport: parallel.TaskSupport = new parallel.ForkJoinTaskSupport(new ForkJoinPool())

  /** Create an `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the order book.
    * @tparam CC type of underlying collection class used to store the `Order` instances.
    */
  def apply[O <: Order, CC <: parallel.mutable.ParMap[UUID, O]](tradable: Tradable)(implicit cbf: CanBuildFrom[_, _, CC], ts: parallel.TaskSupport): OrderBook[O, CC] =  {
    new OrderBook[O, CC](tradable)(cbf, ts)
  }

  /** Create an `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable)(implicit ts: parallel.TaskSupport): OrderBook[O, parallel.mutable.ParHashMap[UUID, O]] =  {
    val cbf =implicitly[CanBuildFrom[_, _, parallel.mutable.ParHashMap[UUID, O]]]
    new OrderBook[O, parallel.mutable.ParHashMap[UUID, O]](tradable)(cbf, ts)
  }

}