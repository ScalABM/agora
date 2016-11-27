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
package org.economicsl.agora.markets.tradables.orders.bid


import java.util.UUID

import org.economicsl.agora.markets.tradables._


/** Trait defining a `MarketBidOrder`. */
trait MarketBidOrder extends LimitBidOrder {
  this: Quantity =>

  /** An issuer of a `MarketBidOrder` is willing to buy at any positive `Price`. */
  val limit = Price.MaxValue

}


/** Companion object for the `MarketBidOrder` trait.
  *
  * Provides constructors for the default implementations of a `MarketBidOrder`.
  */
object MarketBidOrder {

  /** Creates an instance of a `MarketBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketBidOrder` was issued.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    * @note a `MarketBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentMarketBidOrder`.
    */
  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): MarketBidOrder with MultiUnit = {
    MultiUnitImpl(issuer, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `MarketBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    * @note a `MarketBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentMarketBidOrder`.
    */
  def apply(issuer: UUID, timestamp: Long, tradable: Tradable, uuid: UUID): MarketBidOrder with SingleUnit = {
    SingleUnitImpl(issuer, timestamp, tradable, uuid)
  }

  /** Class providing a default implementation of a `MarketBidOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketBidOrder` was issued.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    * @note a `MarketBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentMarketBidOrder`.
    */
  private[this] case class MultiUnitImpl(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketBidOrder with MultiUnit


  /** Class providing a default implementation of a `MarketBidOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    * @note a `MarketBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `MarketBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `MarketBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentMarketBidOrder`.
    */
  private[this] case class SingleUnitImpl(issuer: UUID, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketBidOrder with SingleUnit

}