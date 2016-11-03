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
package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Order

import scala.collection.mutable


/** Class for modeling an `PriorityOrderBook` where the underlying collection of orders is prioritised.
  *
  * @param tradable all `Order` instances contained in the `PriorityOrderBook` should be for the same `Tradable`.
  * @param priority an `Ordering` used to prioritise `Order` instances.
  * @tparam O type of `Order` stored in the order book.
  */
class PriorityOrderBook[O <: Order](tradable: Tradable)(implicit priority: Ordering[O])
  extends OrderBook[O](tradable) with MutableExistingOrders[O] with MutablePrioritisedOrders[O] {

  /* Underlying prioritised collection of `Order` instances. */
  protected[orderbooks] var prioritisedOrders: mutable.PriorityQueue[O] = mutable.PriorityQueue.empty[O](priority)

}


/** Factory for creating `PriorityOrderBook` instances. */
object PriorityOrderBook {

  /** Create a `PriorityOrderBook` for a particular `Tradable`.
    *
    * @param tradable All `Orders` contained in the `PriorityOrderBook` should be for the same `Tradable`.
    * @param priority an `Ordering` used to prioritise `Order` instances.
    * @tparam O type of `Order` stored in the order book.
    */
  def apply[O <: Order](tradable: Tradable)(implicit priority: Ordering[O]): PriorityOrderBook[O] = {
    new PriorityOrderBook(tradable)(priority)
  }

}