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
package markets.tradables.orders.bid

import java.util.UUID

import markets.tradables.orders.ask.{AskOrder, LimitAskOrder, MarketAskOrder}
import markets.tradables.orders.{NonPriceCriteria, PriceCriteria}
import markets.tradables.{LimitPrice, Tradable}


/** Trait the interface for a `LimitBidOrder`. */
trait LimitBidOrder extends BidOrder with LimitPrice with PriceCriteria[AskOrder] with NonPriceCriteria[AskOrder]


/** Companion object for the `LimitBidOrder` trait.
  *
  * The companion object defines various orderings for `LimitBidOrder` instances and provides a constructor for the
  * default implementation of a `LimitBidOrder`.
  */
object LimitBidOrder {

  /** By default, instances of `LimitBidOrder` are ordered based on `limit` price from highest to lowest */
  implicit def ordering[O <: LimitBidOrder]: Ordering[O] = LimitPrice.ordering.reverse

  /** Creates an instance of a `LimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param additionalCriteria a function defining non-price criteria used to determine whether some `BidOrder` is an
    *                           acceptable match for the `LimitBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `LimitBidOrder` was issued.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @return an instance of a `LimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Long, additionalCriteria: Option[(AskOrder) => Boolean], quantity: Long, timestamp: Long,
            tradable: Tradable, uuid: UUID): LimitBidOrder = {
    new DefaultLimitBidOrder(issuer, limit, additionalCriteria, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `LimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `LimitBidOrder` was issued.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @return an instance of a `LimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): LimitBidOrder = {
    new PureLimitBidOrder(issuer, limit, quantity, timestamp, tradable, uuid)
  }

  
  private[this] class DefaultLimitBidOrder(val issuer: UUID, val limit: Long, val nonPriceCriteria: Option[(AskOrder) => Boolean],
                                           val quantity: Long, val timestamp: Long, val tradable: Tradable, val uuid: UUID)
    extends LimitBidOrder {

    val priceCriteria: (AskOrder) => Boolean = {
      case order: MarketAskOrder => order.tradable == this.tradable
      case order: LimitAskOrder => (order.tradable == this.tradable) && (this.limit >= order.limit)
      case _ => false
    }

    /** Boolean function used to determine whether some `AskOrder` is an acceptable match for a `LimitBidOrder`
      *
      * @return a boolean function that returns `true` if the `AskOrder` is acceptable and `false` otherwise.
      */
    val isAcceptable: (AskOrder) => Boolean = nonPriceCriteria match {
      case Some(additionalCriteria) => order => priceCriteria(order) && additionalCriteria(order)
      case None => order => priceCriteria(order)
    }

  }


  private[this] class PureLimitBidOrder(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends DefaultLimitBidOrder(issuer, limit, None, quantity, timestamp, tradable, uuid)

}