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

import org.economicsl.agora.markets.tradables.orders.ask.{AskOrder, LimitAskOrder}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, Tradable}


/** Trait defining a type of `LimitBidOrder` that can be stored in a `BidOrderBook`. */
trait PersistentLimitBidOrder extends LimitBidOrder with Persistent


/** Companion object for the `PersistentLimitBidOrder` trait.
  *
  * The companion object provides a constructor for the default implementation of a `PersistentLimitBidOrder`.
  */
object PersistentLimitBidOrder {

  /** Creates an instance of a `PersistentLimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentLimitBidOrder`.
    * @param limit the minimum price at which the `PersistentLimitBidOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `PersistentLimitBidOrder` was issued.
    * @param timestamp the time at which the `PersistentLimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentLimitBidOrder` was issued.
    * @param uuid the `UUID` of the `PersistentLimitBidOrder`.
    * @return an instance of a `PersistentLimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): PersistentLimitBidOrder = {
    DefaultImpl(issuer, limit, quantity, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `PersistentLimitBidOrder` suitable for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `PersistentLimitBidOrder`.
    * @param limit the minimum price at which the `PersistentLimitBidOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `PersistentLimitBidOrder` was issued.
    * @param timestamp the time at which the `PersistentLimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `PersistentLimitBidOrder` was issued.
    * @param uuid the `UUID` of the `PersistentLimitBidOrder`.
    */
  private[this] case class DefaultImpl(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable,
                                       uuid: UUID)
    extends PersistentLimitBidOrder {

    require(Price.MinValue < limit && limit < Price.MaxValue, "A price value must be strictly positive and finite!")

    val priceCriteria: (AskOrder with Persistent) => Boolean = {
      case order: LimitAskOrder with Persistent => limit >= order.limit
      case _ => false
    }

    val isAcceptable: (AskOrder with Persistent) => Boolean = {
      order => (order.tradable == tradable) && priceCriteria(order)
    }

  }

}