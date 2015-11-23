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

import akka.actor.{Actor, ActorRef, Props}

import markets.{MarketActor, OrderRejected}
import markets.clearing.engines.MatchingEngineLike
import markets.orders.Order
import markets.settlement.SettlementMechanismActor
import markets.settlement.strategies.SettlementStrategy
import markets.tradables.Tradable

import scala.collection.immutable


/** Base class for an `ExchangeActor`.
  *
  * @param matchingEngines a mapping from `Tradable` objects to `MatchingEngineLike` objects used
  *                        to construct a collection of `MarketActor`.
  * @note Users wishing to create their own `ExchangeActor` should do so directly using this
  *       class. For convenience a number of typically use cases for `ExchangeActor` inherit from
  *       this base class.
  */
class ExchangeActor(matchingEngines: immutable.Map[Tradable, MatchingEngineLike],
                    settlementStrategy: SettlementStrategy) extends Actor {

  /* Settlement mechanism is a child of the `ExchangeActor`. */
  val settlementMechanism: ActorRef = {
    context.actorOf(SettlementMechanismActor.props(settlementStrategy), "settlement-mechanism")
  }

  /* Create a market actor for each `Tradable`. */
  var markets: immutable.Map[Tradable, ActorRef] = {
    matchingEngines.map {
      case (tradable, matchingEngine) => tradable -> marketActorFactory(matchingEngine, tradable)
    }
  }

  def marketActorFactory(matchingEngine: MatchingEngineLike, tradable: Tradable): ActorRef = {
    context.actorOf(MarketActor.props(matchingEngine, settlementMechanism, tradable))
  }

  def receive: Receive = {
    case order: Order =>
      markets.get(order.tradable) match {
        case Some(market) => market forward order
        case None => order.issuer ! OrderRejected
      }
    case AddMarket(matchingEngine, tradable) =>  // add a market to the exchange
      val newMarket = marketActorFactory(matchingEngine, tradable)
      markets = markets + (tradable -> newMarket)
    case RemoveMarket(tradable) =>  // remove a market from the exchange
      markets = markets - tradable
    case _ => ???
  }

  case class AddMarket(matchingEngine: MatchingEngineLike, tradable: Tradable)

  case class RemoveMarket(tradable: Tradable)

}


/** Companion object for `ExchangeActor`. */
object ExchangeActor {

  def props(matchingEngines: immutable.Map[Tradable, MatchingEngineLike],
            settlementStrategy: SettlementStrategy): Props = {
    Props(new ExchangeActor(matchingEngines, settlementStrategy))
  }

}
