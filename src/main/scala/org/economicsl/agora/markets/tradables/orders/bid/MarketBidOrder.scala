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
import org.economicsl.agora.markets.tradables.{Price, Tradable}


/** Trait defining the interface for a `MarketBidOrder`. */
trait MarketBidOrder extends LimitBidOrder {

  /** An issuer of a `MarketBidOrder` is willing to buy at any positive `Price`. */
  val limit = Price.MaxValue

}


/** Companion object for the `MarketBidOrder` trait.
  *
  * Provides constructors for the default implementations of a `MarketBidOrder`.
  */
object MarketBidOrder {

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
    DefaultImpl(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
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
    DefaultImpl(issuer, None, quantity, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of a `MarketBidOrder` designed for use in securities market simulations.
    *
    * @param issuer the `UUID` of the actor that issued the `MarketBidOrder`.
    * @param nonPriceCriteria a function defining non-price criteria used to determine whether some `BidOrder` is an
    *                         acceptable match for the `MarketBidOrder`.
    * @param quantity the number of units of the `tradable` for which the `MarketBidOrder` was issued.
    * @param timestamp the time at which the `MarketBidOrder` was issued.
    * @param tradable the `Tradable` for which the `MarketBidOrder` was issued.
    * @param uuid the `UUID` of the `MarketBidOrder`.
    * @return an instance of a `MarketBidOrder`.
    */
  private[this] case class DefaultImpl(issuer: UUID, nonPriceCriteria: Option[(AskOrder) => Boolean], quantity: Long,
                                       timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketBidOrder {

    val priceCriteria: (AskOrder) => Boolean = {
      case order @ (_: MarketBidOrder | _: LimitAskOrder) => order.tradable == this.tradable
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

}