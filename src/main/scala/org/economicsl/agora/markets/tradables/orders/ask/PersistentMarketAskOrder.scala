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

import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{MultiUnit, Quantity, SingleUnit, Tradable}


/** Trait defining a `MarketAskOrder` that can be stored in an `AskOrderBook`. */
trait PersistentMarketAskOrder extends MarketAskOrder with Persistent {
  this: Quantity =>
}


/** Companion object for the `PersistentMarketAskOrder` trait.
  *
  * Provides constructors for the default implementations of a `PersistentMarketAskOrder`.
  */
object PersistentMarketAskOrder {

  /** Creates an instance of a `PersistentMarketAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentMarketAskOrder`.
    * @param quantity the number of units of the `tradable` for which the `PersistentMarketAskOrder` was issued.
    * @param timestamp the time at which the `PersistentMarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentMarketAskOrder` was issued.
    * @param uuid the `UUID` of the `PersistentMarketAskOrder`.
    * @return an instance of a `PersistentMarketAskOrder`.
    */
  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): PersistentMarketAskOrder = {
    MultiUnitImpl(issuer, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `PersistentMarketAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentMarketAskOrder`.
    * @param timestamp the time at which the `PersistentMarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentMarketAskOrder` was issued.
    * @param uuid the `UUID` of the `PersistentMarketAskOrder`.
    * @return an instance of a `PersistentMarketAskOrder`.
    */
  def apply(issuer: UUID, timestamp: Long, tradable: Tradable, uuid: UUID): PersistentMarketAskOrder = {
    SingleUnitImpl(issuer, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `PersistentMarketAskOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentMarketAskOrder`.
    * @param quantity the number of units of the `tradable` for which the `PersistentMarketAskOrder` was issued.
    * @param timestamp the time at which the `PersistentMarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentMarketAskOrder` was issued.
    * @param uuid the `UUID` of the `PersistentMarketAskOrder`.
    * @return an instance of a `PersistentMarketAskOrder`.
    */
  private[this] case class MultiUnitImpl(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends PersistentMarketAskOrder with MultiUnit


  /** Class providing a default implementation of a `PersistentMarketAskOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentMarketAskOrder`.
    * @param timestamp the time at which the `PersistentMarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentMarketAskOrder` was issued.
    * @param uuid the `UUID` of the `PersistentMarketAskOrder`.
    * @return an instance of a `PersistentMarketAskOrder`.
    */
  private[this] case class SingleUnitImpl(issuer: UUID, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends PersistentMarketAskOrder with SingleUnit

}