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

import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.participants.strategies.TradingStrategy
import markets.tickers.Tick
import markets.tradables.Tradable


/** Class representing a stub implementation of the OrderIssuer trait for testing.
  *
  * @param markets
  * @param tickers
  * @param tradingStrategy
  */
class TestOrderIssuer(markets: Map[Tradable, ActorRef],
                      tickers: Map[Tradable, Agent[Tick]],
                      val tradingStrategy: TradingStrategy)
  extends TestMarketParticipant(markets, tickers) with OrderIssuer {

  wrappedBecome(orderIssuerBehavior)

}


object TestOrderIssuer {

  def apply(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]],
            tradingStrategy: TradingStrategy): TestOrderIssuer = {
    new TestOrderIssuer(markets, tickers, tradingStrategy)
  }

  def props(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]],
            tradingStrategy: TradingStrategy): Props = {
    Props(new TestOrderIssuer(markets, tickers, tradingStrategy))
  }
}