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
import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.parallel
import scala.collection.parallel.ParIterable


/** Class for modeling a simple `OrderBook`.
  *
  * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
  * @tparam O type of `Order` stored in the order book.
  * @todo Currently the underlying `existingOrders` will use the JVM default ForkJoinTaskSupport object for scheduling
  *       and load-balancing.  This [[http://docs.scala-lang.org/overviews/parallel-collections/configuration.html can be customized]]
  *       but requires some clear thinking about how to expose this functionality to the user.
  */
class OrderBook[O <: Order](tradable: Tradable) extends generic.OrderBook[O](tradable) {

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
  def filter(p: (O) => Boolean): Option[ParIterable[O]] = {
    val filteredOrders = existingOrders.values.filter(p)
    if (filteredOrders.isEmpty) None else Some(filteredOrders)
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (O) => Boolean): Option[O] = {
    existingOrders.values.find(p)
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
  protected[orderbooks] val existingOrders = parallel.mutable.ParHashMap.empty[UUID, O]

}


/** Factory for creating `OrderBook` instances. */
object OrderBook {

  /** Create and `OrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `OrderBook` should be for the same `Tradable`.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable): OrderBook[O] = new OrderBook[O](tradable)

}