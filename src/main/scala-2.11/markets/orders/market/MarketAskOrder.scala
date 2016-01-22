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
package markets.orders.market

import akka.actor.ActorRef

import java.util.UUID

import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable


class MarketAskOrder(val issuer: ActorRef,
                     val quantity: Long,
                     val timestamp: Long,
                     val tradable: Tradable,
                     val uuid: UUID) extends MarketOrder with AskOrder {

  val isSplittable: Boolean = false

  val price: Long = 0

  /** Determines whether a MarketAskOrder crosses with some BidOrder.
    *
    * @param order some BidOrder.
    * @return true if MarketAskOrder crosses with the BidOrder; false otherwise.
    * @note A MarketAskOrder should cross with any...
    *       1. splittable BidOrder with strictly larger quantity;
    *       2. AskOrder with equal quantity.
    */
  def crosses(order: BidOrder): Boolean = {
    (order.isSplittable && quantity <= order.quantity) || quantity == order.quantity
  }

}


object MarketAskOrder {

  def apply(issuer: ActorRef,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable,
            uuid: UUID): MarketAskOrder = {
    new MarketAskOrder(issuer, quantity, timestamp, tradable, uuid)
  }

}