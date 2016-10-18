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

  /** Boolean function used to determine whether some `BidOrder` is an acceptable match for a `MarketAskOrder`
    *
    * @return a boolean function that returns `true` if the `BidOrder` is acceptable and `false` otherwise.
    */
  def isAcceptable: (BidOrder) => Boolean = {
    case order @ (_: MarketBidOrder | _: LimitBidOrder) => order.tradable == this.tradable
    case _ => false
  }

}


object MarketAskOrder {

  /** By default, instances of `MarketAskOrder` are ordered based on `uuid` price from lowest to highest */
  implicit def ordering[A <: MarketAskOrder]: Ordering[A] = Order.ordering

  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): MarketAskOrder = {
    DefaultMarketAskOrder(issuer, quantity, timestamp, tradable, uuid)
  }

  /** Default implementation of a `MarketAskOrder`.
    *
    * @param issuer
    * @param quantity
    * @param timestamp
    * @param tradable
    * @param uuid
    */
  private[this] case class DefaultMarketAskOrder(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketAskOrder {

    /** Boolean function used to determine whether some `BidOrder` is an acceptable match for a `MarketAskOrder`
      *
      * @return a boolean function that returns `true` if the `BidOrder` is acceptable and `false` otherwise.
      */
    override val isAcceptable: (BidOrder) => Boolean = super.isAcceptable

  }

}