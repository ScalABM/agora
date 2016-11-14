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

import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder, MarketBidOrder}
import org.economicsl.agora.markets.tradables.{Price, Tradable}


/** Trait defining the interface for a `MarketAskOrder`. */
trait MarketAskOrder extends LimitAskOrder {

  /** An issuer of a `MarketAskOrder` is willing to sell for any positive `Price`. */
  val limit = Price(0.0)

}


/** Companion object for the `MarketAskOrder` trait.
  *
  * Provides constructors for the default implementations of a `MarketAskOrder`.
  */
object MarketAskOrder {

  /** Creates an instance of a `MarketAskOrder`.
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
  def apply(issuer: UUID, nonPriceCriteria: Option[(BidOrder) => Boolean], quantity: Long, timestamp: Long,
            tradable: Tradable, uuid: UUID): MarketAskOrder = {
    DefaultImpl(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
  }

  /** Creates an instance of a `MarketAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketAskOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketAskOrder` was issued.
    * @param timestamp the time at which the `MarketAskOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketAskOrder` was issued.
    * @param uuid the `UUID` of the `MarketAskOrder`.
    * @return an instance of a `MarketAskOrder`.
    */
  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): MarketAskOrder = {
    DefaultImpl(issuer, None, quantity, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `MarketAskOrder` designed for use in securities market simulations.
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
  private[this] case class DefaultImpl(issuer: UUID, nonPriceCriteria: Option[(BidOrder) => Boolean], quantity: Long,
                                       timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketAskOrder {

    val priceCriteria: (BidOrder) => Boolean = {
      case order @ (_: MarketBidOrder | _: LimitBidOrder) => order.tradable == this.tradable
      case _ => false
    }

    /** Boolean function used to determine whether some `BidOrder` is an acceptable match for a `MarketAskOrder`
      *
      * @return a boolean function that returns `true` if the `BidOrder` is acceptable and `false` otherwise.
      */
    def isAcceptable: (BidOrder) => Boolean = nonPriceCriteria match {
      case Some(additionalCriteria) => order => priceCriteria(order) && additionalCriteria(order)
      case None => order => priceCriteria(order)
    }

  }

}