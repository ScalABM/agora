/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.orders.limit

import akka.actor.ActorRef

import java.util.UUID

import markets.orders.market.MarketAskOrder
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable


class LimitBidOrder(val issuer: ActorRef,
                    val price: Long,
                    val quantity: Long,
                    val timestamp: Long,
                    val tradable: Tradable,
                    val uuid: UUID) extends LimitOrderLike with BidOrder {

  val isSplittable: Boolean = false

  /** Determines whether a LimitBidOrder crosses with some AskOrder.
    *
    * @param order some AskOrder.
    * @return true if the LimitBidOrder crosses with the AskOrder; false otherwise.
    * @note A LimitBidOrder should cross with any ...
    *       1. SplittableMarketAskOrder with weakly larger quantity;
    *       2. MarketAskOrder with equal quantity;
    *       3. SplittableLimitAskOrder with strictly lower limit price and weakly larger quantity;
    *       4. LimitAskOrder with strictly lower limit price and equal quantity.
    */
  def crosses(order: AskOrder): Boolean = order match {
    case _: MarketAskOrder =>
      (order.isSplittable && quantity <= order.quantity) || quantity == order.quantity
    case _: LimitAskOrder if price > order.price =>
      (order.isSplittable && quantity <= order.quantity) || quantity == order.quantity
    case _ => false
  }

}


object LimitBidOrder {

  def apply(issuer: ActorRef,
            price: Long,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable,
            uuid: UUID): LimitBidOrder = {
    new LimitBidOrder(issuer, price, quantity, timestamp, tradable, uuid)
  }
}
