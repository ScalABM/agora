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
import org.economicsl.agora.markets.tradables.orders.{Persistent, PriceCriteria}
import org.economicsl.agora.markets.tradables._


/** Trait defining a `LimitBidOrder`. */
trait LimitBidOrder extends BidOrder with LimitPrice with PriceCriteria[AskOrder with Persistent] {
  this: Quantity =>

  def priceCriteria: (AskOrder with Persistent) => Boolean = {
    case order: LimitAskOrder with Persistent => limit >= order.limit
    case _ => false
  }

  def isAcceptable: (AskOrder with Persistent) => Boolean = {
    order => (order.tradable == tradable) && priceCriteria(order)
  }
  
}


/** Companion object for the `LimitBidOrder` trait.
  *
  * The companion object defines various orderings for `LimitBidOrder` instances as well as a constructor for the
  * default implementation of `LimitBidOrder`.
  */
object LimitBidOrder {

  /** By default, instances of `LimitBidOrder` are ordered based on `limit` price from highest to lowest */
  implicit def ordering[B <: LimitBidOrder]: Ordering[B] = LimitPrice.ordering.reverse

  /** The highest priority `LimitBidOrder` is the one with the highest `limit` price. */
  def priority[B <: LimitBidOrder]: Ordering[B] = LimitPrice.ordering

  /** Creates an instance of a `LimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `LimitBidOrder` was issued.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @return an instance of a `LimitBidOrder`.
    * @note this `LimitBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `LimitBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `LimitBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentLimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): LimitBidOrder = {
    MultiUnitImpl(issuer, limit, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `LimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @return an instance of a `LimitBidOrder`.
    * @note this `LimitBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `LimitBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `LimitBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentLimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Price, timestamp: Long, tradable: Tradable, uuid: UUID): LimitBidOrder = {
    SingleUnitImpl(issuer, limit, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `LimitBidOrder` suitable for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `LimitBidOrder` was issued.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @note this `LimitBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `LimitBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `LimitBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentLimitBidOrder`.
    */
  private[this] case class MultiUnitImpl(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable,
                                         uuid: UUID)
    extends LimitBidOrder with MultiUnit


  /** Class providing a default implementation of a `LimitBidOrder` suitable for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @note this `LimitBidOrder` is an "Immediate-Or-Cancel (IOC)" order meaning that a `LimitBidOrder` must be filled
    *       (either partially or fully) immediately or be cancelled. If you want a `LimitBidOrder` to persist in an
    *       `BidOrderBook` use a `PersistentLimitBidOrder`.
    */
  private[this] case class SingleUnitImpl(issuer: UUID, limit: Price, timestamp: Long, tradable: Tradable,
                                          uuid: UUID)
    extends LimitBidOrder with SingleUnit

}