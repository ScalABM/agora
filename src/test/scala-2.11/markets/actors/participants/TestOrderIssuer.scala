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
  * @param askOrderIssuingStrategy
  * @param bidOrderIssuingStrategy
  */
class TestOrderIssuer(val askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
                      val bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder])
  extends OrderIssuer {

  var markets = Map.empty[Tradable, ActorRef]

  var tickers = Map.empty[Tradable, Agent[Tick]]

}


object TestOrderIssuer {

  def props(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder]): Props = {
    Props(new TestOrderIssuer(askOrderIssuingStrategy, bidOrderIssuingStrategy))
  }

}