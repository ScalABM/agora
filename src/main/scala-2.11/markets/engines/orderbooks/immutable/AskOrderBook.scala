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

import java.util.UUID

import markets.engines.orderbooks.OrderBook
import markets.orders.AskOrder
import markets.tradables.Tradable

import scala.collection.immutable
import scala.util.{Failure, Success, Try}


/** Class representing and `OrderBook` that contains `AskOrders`.
  *
   * @param tradable All `AskOrder` instances contained in the `AskOrderBook` should be for the
  *                 same `Tradable`.
  */
class AskOrderBook(val tradable: Tradable) extends OrderBook[AskOrder] {

  /** Add an `AskOrder` to the `OrderBook`.
    *
    * @param order the `AskOrder` that should be added to the `AskOrderBook`.
    * @return `Success()` if the `order` is added to the `AskOrderBook`; `Failure(ex)` otherwise.
    * @note Underlying implementation of `existingOrders` uses a `immutable.HashMap` in order to
    *       guarantee that adding an `AskOrder` to the `AskOrderBook` is an `O(1)` operation.
    */
  override def add(order: AskOrder): Try[Unit] = super.add(order) match {
    case Success(()) => Try(existingOrders += (order.uuid -> order))
    case result @ Failure(ex) => result
  }

  /** Remove and return an existing `AskOrder` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note Underlying implementation of `existingOrders` uses a `immutable.HashMap` in order to
    *       guarantee that removing an `AskOrder` from the `AskOrderBook` is an `O(1)` operation.
    */
  def remove(uuid: UUID): Option[AskOrder] = {
    val residualOrder = existingOrders.get(uuid)
    existingOrders -= uuid
    residualOrder
  }

  /* Protected at the package level to simplify testing. */
  @volatile
  protected[orderbooks] var existingOrders = immutable.HashMap.empty[UUID, AskOrder]

}


object AskOrderBook {

  /** Auxiliary constructor for `AskORderBook` class.
    *
    * @param tradable All `AskOrder` instances contained in the `AskOrderBook` should be for the
    *                 same `Tradable`.
    * @return a new `AskOrderBook`.
    */
  def apply(tradable: Tradable): AskOrderBook = new AskOrderBook(tradable)

}
