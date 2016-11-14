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
import org.economicsl.agora.markets.tradables.orders.PriceCriteria
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, Tradable}


/** Trait defining the interface for a `LimitAskOrder`. */
trait LimitAskOrder extends AskOrder with LimitPrice with PriceCriteria[BidOrder]


/** Companion object for the `LimitAskOrder` trait.
  *
  * Provides various orderings for `LimitAskOrder` instances as well as constructors for the default `LimitAskOrder`
  * implementation.
  */
object LimitAskOrder {

  /** By default, instances of `LimitAskOrder` are ordered based on `limit` price from lowest to highest */
  implicit def ordering[A <: LimitAskOrder]: Ordering[A] = LimitPrice.ordering

  /** The highest priority `LimitAskOrder` is the one with the lowest `limit` price. */
  def priority[O <: LimitAskOrder]: Ordering[O] = LimitPrice.ordering.reverse

  /** Creates an instance of a `LimitAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitAskOrder`.
    * @param limit the minimum price at which the `LimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `LimitAskOrder` was issued.
    * @param timestamp the time at which the `LimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitAskOrder` was issued.
    * @param uuid the `UUID` of the `LimitAskOrder`.
    * @return an instance of a `LimitAskOrder`.
    */
  def apply(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): LimitAskOrder = {
    DefaultImpl(issuer, limit, quantity, timestamp, tradable, uuid)
  }

  private[this] case class DefaultImpl(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable,
                                       uuid: UUID)
    extends LimitAskOrder {

    require(Price.MinValue < limit && limit < Price.MaxValue, "A price value must be strictly positive and finite!")

    val priceCriteria: (BidOrder) => Boolean = {
      case order: LimitBidOrder => limit <= order.limit
      case _ => false
    }

    /** Boolean function used to determine whether some `BidOrder` is an acceptable match for a `LimitAskOrder`
      *
      * @return a boolean function that returns `true` if the `BidOrder` is acceptable and `false` otherwise.
      */
    val isAcceptable: (BidOrder) => Boolean = {
      order => (order.tradable == tradable) && priceCriteria(order)
    }

  }

}