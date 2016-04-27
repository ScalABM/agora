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
package markets.actors.participants

import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.actors.participants.strategies.TradingStrategy
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


/** Class representing a stub implementation of the MarketParticipant trait for testing.
  *
  * @param markets
  * @param tickers
  */
class TestMarketParticipant(var markets: Map[Tradable, ActorRef],
                            var tickers: Map[Tradable, Agent[Tick]])
  extends MarketParticipant


object TestMarketParticipant {

  def apply(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]]): TestMarketParticipant = {
    new TestMarketParticipant(markets, tickers)
  }

  def props(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]]): Props = {
    Props(new TestMarketParticipant(markets, tickers))
  }
}