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
package markets.exchanges

import akka.actor.{ActorRef, Props}

import markets.{Cancel, BaseActor}
import markets.clearing.engines.MatchingEngine
import markets.orders.Order


/** Base class for an `ExchangeActor`.
  *
  * @param matchingEngine a mapping from `Tradable` objects to `MatchingEngine` objects used
  *                        to construct a collection of `MarketActor`.
  * @note
  */
class ExchangeActor(val matchingEngine: MatchingEngine,
                    val settlementMechanism: ActorRef) extends ExchangeLike with BaseActor {

  def exchangeActorBehavior: Receive = {
    case order: Order =>  // get (or create) a suitable market actor and forward the order...
      val market = context.child(order.tradable.ticker).getOrElse {
        marketActorFactory(order.tradable)
      }
      market forward order
    case message @ Cancel(order, _, _) =>
      val market = context.child(order.tradable.ticker).getOrElse {
        ???  // @todo This should never happen!
      }
      market forward message
  }

  def receive: Receive = {
    exchangeActorBehavior orElse baseActorBehavior
  }

}


/** Companion object for `ExchangeActor`. */
object ExchangeActor {

  def props(matchingEngine: MatchingEngine, settlementMechanism: ActorRef): Props = {
    Props(new ExchangeActor(matchingEngine, settlementMechanism))
  }

}
