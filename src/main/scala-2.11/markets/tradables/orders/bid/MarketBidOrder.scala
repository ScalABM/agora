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
import markets.tradables.orders.{MarketOrder, Order, Predicate}
import markets.tradables.Tradable


/** Trait defining an order to buy some `Tradable` at any price. */
trait MarketBidOrder extends BidOrder with MarketOrder with Predicate[AskOrder] {

  /** Non-price criteria used to determine whether some `AskOrder` is an acceptable match for a `MarketBidOrder`. */
  def nonPriceCriteria: Option[(AskOrder) => Boolean]

  /** Boolean function used to determine whether some `AskOrder` is an acceptable match for a `MarketBidOrder`
    *
    * @return a boolean function that returns `true` if the `AskOrder` is acceptable and `false` otherwise.
    */
  def isAcceptable: (AskOrder) => Boolean = nonPriceCriteria match {
    case Some(additionalCriteria) => order => priceCriteria(order) && additionalCriteria(order)
    case None => order => priceCriteria(order)
  }

  protected def priceCriteria: (AskOrder) => Boolean = {
    case order @ (_: MarketAskOrder | _: LimitAskOrder) => order.tradable == this.tradable
    case _ => false
  }

}


object MarketBidOrder {

  implicit def ordering[B <: MarketBidOrder]: Ordering[B] = Order.ordering

  /** Creates an instance of a `MarketBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param nonPriceCriteria a function defining non-price criteria used to determine whether some `AskOrder` is an
    *                         acceptable match for the `MarketBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketBidOrder` was issued.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    */
  def apply(issuer: UUID, nonPriceCriteria: Option[(AskOrder) => Boolean], quantity: Long, timestamp: Long,
            tradable: Tradable, uuid: UUID): MarketBidOrder = {
    DefaultMarketBidOrder(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
  }

  private[this] case class DefaultMarketBidOrder(issuer: UUID, nonPriceCriteria: Option[(AskOrder) => Boolean],
                                                 quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketBidOrder {

    override val isAcceptable: (AskOrder) => Boolean = super.isAcceptable

    override protected val priceCriteria: (AskOrder) => Boolean = super.priceCriteria

  }

}