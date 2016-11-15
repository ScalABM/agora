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

import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{Price, Tradable}


/** Trait defining a type of `LimitAskOrder` that can be stored in an `AskOrderBook`. */
trait PersistentLimitAskOrder extends LimitAskOrder with Persistent


/** Companion object for the `PersistentLimitAskOrder` trait.
  *
  * Provides constructors for a default implementation of the `PersistentLimitAskOrder` trait.
  */
object PersistentLimitAskOrder {

  /** Creates an instance of a `PersistentLimitAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentLimitAskOrder`.
    * @param limit the minimum price at which the `PersistentLimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `PersistentLimitAskOrder` was issued.
    * @param timestamp the time at which the `PersistentLimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentLimitAskOrder` was issued.
    * @param uuid the `UUID` of the `PersistentLimitAskOrder`.
    * @return an instance of a `PersistentLimitAskOrder`.
    */
  def apply(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): PersistentLimitAskOrder = {
    DefaultImpl(issuer, limit, quantity, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `PersistentLimitAskOrder` suitable for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentLimitAskOrder`.
    * @param limit the minimum price at which the `PersistentLimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `PersistentLimitAskOrder` was issued.
    * @param timestamp the time at which the `PersistentLimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentLimitAskOrder` was issued.
    * @param uuid the `UUID` of the `PersistentLimitAskOrder`.
    * @return an instance of a `PersistentLimitAskOrder`.
    */
  private[this] case class DefaultImpl(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable,
                                       uuid: UUID)
    extends PersistentLimitAskOrder {

    require(Price.MinValue < limit && limit < Price.MaxValue, "A price value must be strictly positive and finite!")

    val priceCriteria: (BidOrder with Persistent) => Boolean = {
      case order: LimitBidOrder with Persistent => limit <= order.limit
      case _ => false
    }

    val isAcceptable: (BidOrder with Persistent) => Boolean = {
      order => (order.tradable == tradable) && priceCriteria(order)
    }

  }

}