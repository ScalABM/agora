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
import markets.tradables.Tradable
import markets.tradables.orders.{MarketOrder, Order, Predicate}


trait MarketBidOrder extends BidOrder with MarketOrder with Predicate[AskOrder] {

  /** Boolean function used to determine whether some `AskOrder` is an acceptable match for a `MarketBidOrder`
    *
    * @return a boolean function that returns `true` if the `AskOrder` is acceptable and `false` otherwise.
    */
  def isAcceptable: (AskOrder) => Boolean = {
    case order @ (_: MarketAskOrder | _: LimitAskOrder) => order.tradable == this.tradable
    case _ => false
  }

}


object MarketBidOrder {

  implicit def ordering[B <: MarketBidOrder]: Ordering[B] = Order.ordering

  def apply(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): MarketBidOrder = {
    DefaultMarketBidOrder(issuer, quantity, timestamp, tradable, uuid)
  }

  private case class DefaultMarketBidOrder(issuer: UUID, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends MarketBidOrder

}