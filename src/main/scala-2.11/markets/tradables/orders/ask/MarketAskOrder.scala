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
package markets.tradables.orders.ask

import java.util.UUID

import markets.tradables.orders.bid.{BidOrder, LimitBidOrder, MarketBidOrder}
import markets.tradables.orders.{MarketOrder, Order, Predicate}
import markets.tradables.Tradable


/** Trait defining an order to sell some `Tradable` at any price. */
trait MarketAskOrder extends AskOrder with MarketOrder with Predicate[BidOrder] {

  /** Non-price criteria used to determine whether some `BidOrder` is an acceptable match for a `MarketAskOrder`. */
  def nonPriceCriteria: Option[(BidOrder) => Boolean]

  /** Boolean function used to determine whether some `BidOrder` is an acceptable match for a `MarketAskOrder`
    *
    * @return a boolean function that returns `true` if the `BidOrder` is acceptable and `false` otherwise.
    */
  def isAcceptable: (BidOrder) => Boolean = nonPriceCriteria match {
    case Some(additionalCriteria) => order => priceCriteria(order) && additionalCriteria(order)
    case None => order => priceCriteria(order)
  }

  protected def priceCriteria: (BidOrder) => Boolean = {
    case order @ (_: MarketBidOrder | _: LimitBidOrder) => order.tradable == this.tradable
    case _ => false
  }

}


object MarketAskOrder {

  /** By default, instances of `MarketAskOrder` are ordered based on `uuid` price from lowest to highest */
  implicit def ordering[A <: MarketAskOrder]: Ordering[A] = Order.ordering

  def apply(issuer: UUID, nonPriceCriteria: Option[(BidOrder) => Boolean], quantity: Long, timestamp: Long,
            tradable: Tradable, uuid: UUID): MarketAskOrder = {
    DefaultMarketAskOrder(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
  }

  /** Default implementation of a `MarketAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketAskOrder`.
    * @param nonPriceCriteria a function defining non-price criteria used to determine whether some `BidOrder` is an
    *                         acceptable match for the `MarketAskOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketAskOrder` was issued.
    * @param timestamp the time at which the `MarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketAskOrder` was issued.
    * @param uuid the `UUID` of the `MarketAskOrder`.
    * @return an instance of a `MarketAskOrder`.
    */
  private[this] case class DefaultMarketAskOrder(issuer: UUID, nonPriceCriteria: Option[(BidOrder) => Boolean],
                                                 quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketAskOrder {

    override val isAcceptable: (BidOrder) => Boolean = super.isAcceptable

    override protected val priceCriteria: (BidOrder) => Boolean = super.priceCriteria

  }

}