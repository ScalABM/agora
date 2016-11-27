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
package org.economicsl.agora.markets.tradables.orders.ask

import java.util.UUID

import org.economicsl.agora.markets.tradables._
import org.economicsl.agora.markets.tradables.orders.Persistent


/** Trait defining a `MarketAskOrder`. */
trait MarketAskOrder extends LimitAskOrder with Persistent {
  this: Quantity =>

  /** An issuer of a `MarketAskOrder` is willing to sell for any positive `Price`. */
  val limit = Price.MinValue

}


/** Companion object for the `MarketAskOrder` trait.
  *
  * Provides constructors for the default implementations of a `MarketAskOrder`.
  */
object MarketAskOrder {

  /** Creates an instance of a `MarketAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketAskOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketAskOrder` was issued.
    * @param timestamp the time at which the `MarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketAskOrder` was issued.
    * @param uuid the `UUID` of the `MarketAskOrder`.
    * @return an instance of a `MarketAskOrder`.
    * @note a `MarketAskOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketAskOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketAskOrder` to persist in an
    *       `AskOrderBook` use a `PersistentMarketAskOrder`.
    */
  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): MarketAskOrder = {
    MultiUnitImpl(issuer, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `MarketAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketAskOrder`.
    * @param timestamp the time at which the `MarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketAskOrder` was issued.
    * @param uuid the `UUID` of the `MarketAskOrder`.
    * @return an instance of a `MarketAskOrder`.
    * @note a `MarketAskOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketAskOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketAskOrder` to persist in an
    *       `AskOrderBook` use a `PersistentMarketAskOrder`.
    */
  def apply(issuer: UUID, timestamp: Long, tradable: Tradable, uuid: UUID): MarketAskOrder = {
    SingleUnitImpl(issuer, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `MarketAskOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketAskOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketAskOrder` was issued.
    * @param timestamp the time at which the `MarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketAskOrder` was issued.
    * @param uuid the `UUID` of the `MarketAskOrder`.
    * @return an instance of a `MarketAskOrder`.
    * @note a `MarketAskOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketAskOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketAskOrder` to persist in an
    *       `AskOrderBook` use a `PersistentMarketAskOrder`.
    */
  private[this] case class MultiUnitImpl(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketAskOrder with MultiUnit


  /** Class providing a default implementation of a `MarketAskOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketAskOrder`.
    * @param timestamp the time at which the `MarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketAskOrder` was issued.
    * @param uuid the `UUID` of the `MarketAskOrder`.
    * @return an instance of a `MarketAskOrder`.
    * @note a `MarketAskOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketAskOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketAskOrder` to persist in an
    *       `AskOrderBook` use a `PersistentMarketAskOrder`.
    */
  private[this] case class SingleUnitImpl(issuer: UUID, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketAskOrder with SingleUnit

}