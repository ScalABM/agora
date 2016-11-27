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

import org.economicsl.agora.markets.tradables.{LimitPrice, Price, Quantity, Tradable}
import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder}
import org.economicsl.agora.markets.tradables.orders.{Persistent, PriceCriteria}


/** Trait defining a `LimitAskOrder`. */
trait LimitAskOrder extends AskOrder with LimitPrice with PriceCriteria[BidOrder with Persistent] with Quantity {

  def priceCriteria: (BidOrder with Persistent) => Boolean = {
    case order: LimitBidOrder with Persistent => limit <= order.limit
    case _ => false
  }

  def isAcceptable: (BidOrder with Persistent) => Boolean = {
    order => (order.tradable == tradable) && priceCriteria(order)
  }

}


/** Companion object for the `LimitAskOrder` trait.
  *
  * Provides various orderings for `LimitAskOrder` instances as well as a constructor for the default implementation
  * of a `LimitAskOrder`.
  */
object LimitAskOrder {

  /** By default, instances of `LimitAskOrder` are ordered based on `limit` price from lowest to highest */
  implicit def ordering[A <: LimitAskOrder]: Ordering[A] = LimitPrice.ordering

  /** The highest priority `LimitAskOrder` is the one with the lowest `limit` price. */
  def priority[A <: LimitAskOrder]: Ordering[A] = LimitPrice.ordering.reverse

  /** Creates an instance of a `LimitAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitAskOrder`.
    * @param limit the minimum price at which the `LimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `LimitAskOrder` was issued.
    * @param timestamp the time at which the `LimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitAskOrder` was issued.
    * @param uuid the `UUID` of the `LimitAskOrder`.
    * @return an instance of a `LimitAskOrder`.
    * @note this `LimitAskOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `LimitAskOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `LimitAskOrder` to persist in an
    *       `AskOrderBook` use a `PersistentLimitAskOrder`.
    */
  def apply(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): LimitAskOrder = {
    DefaultImpl(issuer, limit, quantity, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `LimitAskOrder` suitable for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitAskOrder`.
    * @param limit the minimum price at which the `LimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `LimitAskOrder` was issued.
    * @param timestamp the time at which the `LimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitAskOrder` was issued.
    * @param uuid the `UUID` of the `LimitAskOrder`.
    * @return an instance of a `LimitAskOrder`.
    * @note this `LimitAskOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `LimitAskOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `LimitAskOrder` to persist in an
    *       `AskOrderBook` use a `PersistentLimitAskOrder`.
    */
  private[this] case class DefaultImpl(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends LimitAskOrder

}