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
import markets.tradables.orders.Predicate
import markets.tradables.{LimitPrice, Tradable}


/** Trait defining an order to buy some `Tradable` at a price less than or equal to some limit price. */
trait LimitBidOrder extends BidOrder with LimitPrice with Predicate[AskOrder] {

  /** Non-price criteria used to determine whether some `AskOrder` is an acceptable match for a `LimitBidOrder`. */
  def nonPriceCriteria: Option[(AskOrder) => Boolean]

  /** Boolean function used to determine whether some `AskOrder` is an acceptable match for a `LimitBidOrder`
    *
    * @return a boolean function that returns `true` if the `AskOrder` is acceptable and `false` otherwise.
    */
  def isAcceptable: (AskOrder) => Boolean = nonPriceCriteria match {
    case Some(additionalCriteria) => order => priceCriteria(order) && additionalCriteria(order)
    case None => order => priceCriteria(order)
  }

  protected def priceCriteria: (AskOrder) => Boolean = {
    case order: MarketAskOrder => order.tradable == this.tradable
    case order: LimitAskOrder => (order.tradable == this.tradable) && (this.limit >= order.limit)
    case _ => false
  }

}


/** Companion object for the `LimitBidOrder` trait.
  *
  * The companion object defines various orderings for `LimitBidOrder` instances and provides a constructor for the
  * default implementation of a `LimitBidOrder`.
  */
object LimitBidOrder {

  /** By default, instances of `LimitBidOrder` are ordered based on `limit` price from highest to lowest */
  implicit def ordering[O <: LimitBidOrder]: Ordering[O] = LimitPrice.ordering.reverse

  /** The highest priority `LimitBidOrder` is the one with the highest `limit` price. */
  def priority[O <: LimitBidOrder]: Ordering[O] = LimitPrice.ordering

  /** Creates an instance of a `LimitBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `LimitBidOrder`.
    * @param limit the minimum price at which the `LimitBidOrder` can be executed.
    * @param nonPriceCriteria a function defining non-price criteria used to determine whether some `AskOrder` is an
    *                         acceptable match for the `LimitBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `LimitBidOrder` was issued.
    * @param timestamp the time at which the `LimitBidOrder` was issued.
    * @param tradable the `Tradable` for which the `LimitBidOrder` was issued.
    * @param uuid the `UUID` of the `LimitBidOrder`.
    * @return an instance of a `LimitBidOrder`.
    */
  def apply(issuer: UUID, limit: Long, nonPriceCriteria: Option[(AskOrder) => Boolean], quantity: Long, timestamp: Long,
            tradable: Tradable, uuid: UUID): LimitBidOrder = {
    DefaultLimitBidOrder(issuer, limit, nonPriceCriteria, quantity, timestamp, tradable, uuid)
  }

  private[this] case class DefaultLimitBidOrder(issuer: UUID, limit: Long, nonPriceCriteria: Option[(AskOrder) => Boolean],
                                                quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends LimitBidOrder {

    override val isAcceptable: (AskOrder) => Boolean = super.isAcceptable

    override protected val priceCriteria: (AskOrder) => Boolean = super.priceCriteria

  }

}