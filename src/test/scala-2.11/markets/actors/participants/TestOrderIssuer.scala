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

import markets.actors.participants.strategies.OrderIssuingStrategy
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable


/** A stub implementation of the `OrderIssuer` trait for testing purposes only.
  *
  * @param markets
  * @param tickers
  * @param askOrderIssuingStrategy
  * @param bidOrderIssuingStrategy
  */
class TestOrderIssuer(markets: Map[Tradable, ActorRef],
                      tickers: Map[Tradable, Agent[Tick]],
                      val askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
                      val bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder])
  extends TestMarketParticipant(markets, tickers)
  with OrderIssuer {

  wrappedBecome(orderIssuerBehavior)

}


object TestOrderIssuer {

  def apply(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]],
            askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder]): TestOrderIssuer = {
    new TestOrderIssuer(markets, tickers, askOrderIssuingStrategy, bidOrderIssuingStrategy)
  }

  def props(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]],
            askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder]): Props = {
    Props(TestOrderIssuer(markets, tickers, askOrderIssuingStrategy, bidOrderIssuingStrategy))
  }

}