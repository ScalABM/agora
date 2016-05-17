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

import markets.orders.{AskOrder, BidOrder, Order}
import markets.actors.participants.strategies.{OrderCancellationStrategy, OrderIssuingStrategy}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


class TestOrderCanceler(markets: Map[Tradable, ActorRef],
                        tickers: Map[Tradable, Agent[Tick]],
                        askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
                        bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
                        val orderCancellationStrategy: OrderCancellationStrategy)
  extends TestOrderIssuer(markets, tickers, askOrderIssuingStrategy, bidOrderIssuingStrategy)
  with OrderCanceler {

  val outstandingOrders = mutable.Set.empty[Order]

  wrappedBecome(orderCancelerBehavior)

}


object TestOrderCanceler {

  def apply(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]],
            askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
            cancellationStrategy: OrderCancellationStrategy): TestOrderCanceler = {
    new TestOrderCanceler(markets, tickers, askOrderIssuingStrategy, bidOrderIssuingStrategy,
      cancellationStrategy)
  }

  def props(markets: Map[Tradable, ActorRef],
            tickers: Map[Tradable, Agent[Tick]],
            askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
            cancellationStrategy: OrderCancellationStrategy): Props = {
    Props(new TestOrderCanceler(markets, tickers, askOrderIssuingStrategy, bidOrderIssuingStrategy,
      cancellationStrategy))
  }

}