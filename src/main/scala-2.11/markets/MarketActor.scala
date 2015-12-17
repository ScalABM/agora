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
package markets

import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.clearing.ClearingMechanismActor
import markets.clearing.engines.MatchingEngine
import markets.tickers.Tick
import markets.tradables.Tradable


/** Actor for modeling markets.
  *
  * A `MarketActor` actor should directly receive `AskOrder` and `BidOrder` orders for a
  * particular `Tradable` (filtering out any invalid orders) and then forward along all valid
  * orders to a `ClearingMechanismActor` for further processing.
  *
  * @param matchingEngine The `MarketActor` uses the `matchingEngine` to construct a
  *                       `ClearingMechanismActor`.
  * @param settlementMechanism The `MarketActor` uses the `settlementMechanism` to construct a
  *                            `ClearingMechanismActor`.
  * @param tradable The object being traded on the market.
  */
class MarketActor(matchingEngine: MatchingEngine,
                  settlementMechanism: ActorRef,
                  ticker: Agent[Tick],
                  val tradable: Tradable) extends BaseActor with MarketLike {

  /** Each `MarketActor` has a unique clearing mechanism. */
  val clearingMechanism: ActorRef = {
    context.actorOf(ClearingMechanismActor.props(matchingEngine, settlementMechanism, ticker),
      "clearing-mechanism")
  }

  def receive: Receive = {
    marketActorBehavior orElse baseActorBehavior
  }

}


/** Companion object for the `MarketActor`. */
object MarketActor {

  def apply(matchingEngine: MatchingEngine,
            settlementMechanism: ActorRef,
            ticker: Agent[Tick],
            tradable: Tradable): MarketActor = {
    new MarketActor(matchingEngine, settlementMechanism, ticker, tradable)
  }

  def props(matchingEngine: MatchingEngine,
            settlementMechanism: ActorRef,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(MarketActor(matchingEngine, settlementMechanism, ticker, tradable))
  }

}
