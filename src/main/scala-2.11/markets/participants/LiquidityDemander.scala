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


import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.participants.strategies.MarketOrderTradingStrategy
import markets.tradables.Tradable


trait LiquidityDemander[M <: MarketOrderTradingStrategy] extends MarketParticipant {

  def marketOrderTradingStrategy: M

  override def receive: Receive = {
    case SubmitMarketAskOrder =>
      marketOrderTradingStrategy.askOrderStrategy(tickers) match {
        case Some((quantity, tradable)) =>
          val marketAskOrder = generateMarketAskOrder(quantity, tradable)
          submit(marketAskOrder)
        case None =>  // No feasible marketAskOrderStrategy!
      }
    case SubmitMarketBidOrder =>
      marketOrderTradingStrategy.bidOrderStrategy(tickers) match {
        case Some((quantity, tradable)) =>
          val marketBidOrder = generateMarketBidOrder(quantity, tradable)
          submit(marketBidOrder)
        case None =>  // No feasible marketBidOrderStrategy!
      }
    case message => super.receive(message)
  }

  protected class SubmitMarketOrder

  protected object SubmitMarketAskOrder extends SubmitMarketOrder

  protected object SubmitMarketBidOrder extends SubmitMarketOrder

  private[this] final def generateMarketAskOrder(quantity: Long, tradable: Tradable) = {
    MarketAskOrder(self, quantity, timestamp(), tradable, uuid())
  }

  private[this] final def generateMarketBidOrder(quantity: Long, tradable: Tradable) = {
    MarketBidOrder(self, quantity, timestamp(), tradable, uuid())
  }

}

