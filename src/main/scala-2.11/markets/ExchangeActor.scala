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

import markets.engines.MatchingEngine
import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.immutable


/** Class representing a collection of `MarketActor` using the same type of matching engines and
  * sharing a common settlement mechanism.
  *
  * @param matchingEngine the type of matching engine used by each market whose tradable is listed
  *                       on the exchange.
  * @param settlementMechanism the common settlement mechanism actor shared by each market whose
  *                            tradable is listed on the exchange.
  */
case class ExchangeActor(matchingEngine: MatchingEngine, settlementMechanism: ActorRef)
  extends StackableActor {

  wrappedBecome(exchangeActorBehavior)

  def exchangeActorBehavior: Receive = {
    case order: Order =>
      val market = context.child(order.tradable.symbol).getOrElse {
        marketActorFactory(order.tradable)
      }
      market forward order
    case message @ Cancel(order, _, _) =>
      val market = context.child(order.tradable.symbol).getOrElse {
        marketActorFactory(order.tradable)  // this should never happen!
      }
      market forward message
  }

  def marketActorFactory(tradable: Tradable): ActorRef = {
    val ticker = Agent(immutable.Seq.empty[Tick])(context.system.dispatcher)
    val marketProps = MarketActor.props(matchingEngine, settlementMechanism, ticker, tradable)
    context.actorOf(marketProps, tradable.symbol)
  }

}


/** Companion object for `ExchangeActor`. */
object ExchangeActor {

  def props(matchingEngine: MatchingEngine, settlementMechanism: ActorRef): Props = {
    Props(ExchangeActor(matchingEngine, settlementMechanism))
  }

}
