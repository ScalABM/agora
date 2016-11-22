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

import java.util.UUID

import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.Tradable

import scala.collection.mutable


/** Trait defining a type of `OrderBook` for storing `AskOrder with Persistent` instances. */
trait AskOrderBook[A <: AskOrder with Persistent] extends OrderBook[A]


/** Companion object for the `AskOrderBook` trait.
  *
  * Provides a constructor for the default implementation of the `AskOrderBook` trait.
  */
object AskOrderBook {

  /** Create an `AskOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `AskOrder` instances contained in the `AskOrderBook` should be for the same `Tradable`.
    * @tparam A type of `Order` stored in the order book.
    */
  def apply[A <: AskOrder with Persistent](tradable: Tradable): AskOrderBook[A] = DefaultImpl[A](tradable)


  /** Class providing the default implementation of the `AskOrderBook` trait.
    *
    * @param tradable all `AskOrder` instances contained in the `AskOrderBook` should be for the same `Tradable`.
    * @tparam A the type of `AskOrder with Persistent` stored in the `AskOrderBook`
    */
  private[this] case class DefaultImpl[A <: AskOrder with Persistent](tradable: Tradable) extends AskOrderBook[A] {

    /* underlying collection used to store AskOrder` instances. */
    protected[orderbooks] val existingOrders = mutable.HashMap.empty[UUID, A]

  }

}