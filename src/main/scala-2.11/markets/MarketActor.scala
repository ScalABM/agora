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

import akka.actor.{ActorRef, Props, Actor}
import markets.clearing.ClearingMechanismActor
import markets.clearing.engines.MatchingEngineLike
import markets.orders.OrderLike
import markets.tradables.Tradable


/** Actor for modeling markets.
  *
  * A `MarketActor` actor should directly receive `AskOrderLike` and `BidOrderLike` orders for a
  * particular `Tradable` (filtering out any invalid orders) and then forward along all valid
  * orders to a `ClearingMechanismActor` for further processing.
  * @param matchingEngine The `MarketActor` uses the `matchingEngine` to construct a
  *                       `ClearingMechanismActor`.
  * @param settlementMechanism The `MarketActor` uses the `settlementMechanism` to construct a
  *                            `ClearingMechanismActor`.
  * @param tradable The object being traded on the market.
  */
class MarketActor(matchingEngine: MatchingEngineLike,
                  settlementMechanism: ActorRef,
                  val tradable: Tradable) extends Actor {

  /** Each `MarketActor` has a unique clearing mechanism. */
  val clearingMechanism: ActorRef = {
    context.actorOf(ClearingMechanismActor.props(matchingEngine, settlementMechanism),
      "clearing-mechanism")
  }

  def receive: Receive = {
    case order: OrderLike if order.tradable == tradable =>
      clearingMechanism forward order
      sender() ! OrderAccepted
    case order: OrderLike if !(order.tradable == tradable) =>
      sender() ! OrderRejected
    case _ => ???
  }

}


object MarketActor {

  def apply(matchingEngine: MatchingEngineLike, settlementMechanism: ActorRef, tradable: Tradable): MarketActor = {
    new MarketActor(matchingEngine, settlementMechanism, tradable)
  }

  def props(matchingEngine: MatchingEngineLike, settlementMechanism: ActorRef, tradable: Tradable): Props = {
    Props(new MarketActor(matchingEngine, settlementMechanism, tradable))
  }

}
