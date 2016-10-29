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
package org.economicsl.agora.tradables.orders.bid

import java.util.UUID

import org.economicsl.agora.tradables.orders.ask.{AskOrder, LimitAskOrder, MarketAskOrder}
import org.economicsl.agora.tradables.orders._
import org.economicsl.agora.tradables.Tradable


/** Trait defining an order to buy some `Tradable` at any price. */
trait MarketBidOrder extends BidOrder with MarketOrder with PriceCriteria[AskOrder] with NonPriceCriteria[AskOrder]


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
    new DefaultMarketBidOrder(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `MarketBidOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketBidOrder` was issued.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    */
  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): MarketBidOrder = {
    new PureMarketBidOrder(issuer, quantity, timestamp, tradable, uuid)
  }


  private[this] class DefaultMarketBidOrder(val issuer: UUID, val nonPriceCriteria: Option[(AskOrder) => Boolean],
                                            val quantity: Long, val timestamp: Long, val tradable: Tradable, val uuid: UUID)
    extends MarketBidOrder {

    val priceCriteria: (AskOrder) => Boolean = {
      case order @ (_: MarketAskOrder | _: LimitAskOrder) => order.tradable == this.tradable
      case _ => false
    }

    /** Boolean function used to determine whether some `AskOrder` is an acceptable match for a `MarketBidOrder`
      *
      * @return a boolean function that returns `true` if the `AskOrder` is acceptable and `false` otherwise.
      */
    val isAcceptable: (AskOrder) => Boolean = nonPriceCriteria match {
      case Some(additionalCriteria) => order => priceCriteria(order) && additionalCriteria(order)
      case None => order => priceCriteria(order)
    }

  }


  private[this] class PureMarketBidOrder(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends DefaultMarketBidOrder(issuer, None, quantity, timestamp, tradable, uuid)

}