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
package exchanges

import akka.actor.{ActorRef, Actor}
import markets.MarketActor
import markets.clearing.engines.MatchingEngineLike
import markets.orders.OrderLike
import markets.settlement.SettlementMechanismActor
import markets.tradables.Tradable

import scala.collection.mutable


class ExchangeActor(matchingEngines: mutable.Seq[MatchingEngineLike],
                    tradables: mutable.Seq[Tradable]) extends Actor {

  /* Settlement mechanism is a child of the ExchangeLike. */
  val settlementMechanism: ActorRef = {
    context.actorOf(SettlementMechanismActor.props(), "settlement-mechanism")
  }

  /* Create a market actor for each security in tickers. */
  val markets: mutable.Map[Tradable, ActorRef] = {
    ???
  }

  def marketActorFactory(matchingEngine: MatchingEngineLike, tradable: Tradable): ActorRef = {
    context.actorOf(MarketActor.props(matchingEngine, settlementMechanism, tradable))
  }

  def receive: Receive = {
    case order: OrderLike =>
      markets.get(order.tradable) match {
        case Some(market) => market forward order
        case None =>
      }
    case AddMarket(matchingEngine, tradable) =>  // add a market to the exchange
      val newMarket = marketActorFactory(matchingEngine, tradable)
      markets += (tradable -> newMarket)
    case RemoveMarket(tradable) =>  // remove a market from the exchange
      markets -= tradable
    case _ => ???
  }

  case class AddMarket(matchingEngine: MatchingEngineLike, tradable: Tradable)

  case class RemoveMarket(tradable: Tradable)

}
