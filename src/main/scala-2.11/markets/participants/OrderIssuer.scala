/*
Copyright 2016 David R. Pugh

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
package markets.participants

import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.participants.strategies.TradingStrategy
import markets.tradables.Tradable


trait OrderIssuer extends MarketParticipant {

  def tradingStrategy: TradingStrategy

  override def receive: Receive = {
    case SubmitAskOrder =>
      tradingStrategy.askOrderStrategy(tickers) match {
        case Some((price, quantity, tradable)) =>
          val askOrder = generateAskOrder(price, quantity, tradable)
          submit(askOrder)
        case None =>  // no feasible askOrderStrategy!
      }
    case SubmitBidOrder =>
      tradingStrategy.bidOrderStrategy(tickers) match {
        case Some((price, quantity, tradable)) =>
          val bidOrder = generateBidOrder(price, quantity, tradable)
          submit(bidOrder)
        case None =>  // no feasible bidOrderStrategy!
      }
    case message => super.receive(message)
  }

  private[this] def generateAskOrder(price: Option[Long], quantity: Long, tradable: Tradable) = {
    price match {
      case Some(limitPrice) =>
        LimitAskOrder(self, limitPrice, quantity, timestamp(), tradable, uuid())
      case None =>
        MarketAskOrder(self, quantity, timestamp(), tradable, uuid())
    }
  }

  private[this] def generateBidOrder(price: Option[Long], quantity: Long, tradable: Tradable) = {
    price match {
      case Some(limitPrice) =>
        LimitBidOrder(self, limitPrice, quantity, timestamp(), tradable, uuid())
      case None =>
        MarketBidOrder(self, quantity, timestamp(), tradable, uuid())
    }
  }
}
