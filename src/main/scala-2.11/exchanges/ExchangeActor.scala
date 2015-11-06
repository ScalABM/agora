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

import akka.actor.{Props, ActorRef, Actor}
import markets.{OrderRejected, MarketActor}
import markets.clearing.engines.MatchingEngineLike
import markets.orders.OrderLike
import markets.settlement.SettlementMechanismActor
import markets.tradables.Tradable

import scala.collection.mutable


/** Base class for an `ExchangeActor`.
  *
  * @param matchingEngines a mapping from `Tradable` objects to `MatchingEngineLike` objects used to construct
  *                        a collection of `MarketActor`.
  * @note Users wishing to create their own `ExchangeActor` should do so directly using this class. For convenience a
  *       number of typically use cases for `ExchangeActor` inherit from this base class.
  */
class ExchangeActor(matchingEngines: mutable.Map[Tradable, MatchingEngineLike]) extends Actor {

  /* Settlement mechanism is a child of the ExchangeLike. */
  val settlementMechanism: ActorRef = {
    context.actorOf(SettlementMechanismActor.props(), "settlement-mechanism")
  }

  /* Create a market actor for each security in tickers. */
  val markets: mutable.Map[Tradable, ActorRef] = {
    matchingEngines.map {
      case (tradable, matchingEngine) => tradable -> marketActorFactory(matchingEngine, tradable)
    }
  }

  def marketActorFactory(matchingEngine: MatchingEngineLike, tradable: Tradable): ActorRef = {
    context.actorOf(MarketActor.props(matchingEngine, settlementMechanism, tradable))
  }

  def receive: Receive = {
    case order: OrderLike =>
      markets.get(order.tradable) match {
        case Some(market) => market forward order
        case None => order.issuer ! OrderRejected
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


/** Companion object for `ExchangeActor`. */
object ExchangeActor {

  def apply(matchingEngines: mutable.Map[Tradable, MatchingEngineLike]): ExchangeActor = {
    new ExchangeActor(matchingEngines)
  }

  def props(matchingEngines: mutable.Map[Tradable, MatchingEngineLike]): Props = {
    Props(new ExchangeActor(matchingEngines))
  }
}
