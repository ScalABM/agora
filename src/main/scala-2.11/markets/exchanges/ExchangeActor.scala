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

import markets.BaseActor
import markets.clearing.engines.MatchingEngine


/** Class representing a collection of `MarketActor` using the same type of matching engines and
  * sharing a common settlement mechanism.
  *
  * @param matchingEngine the `MatchingEngine` used by all `MarketActors` that are members of the
  *                       Exchange.
  */
class ExchangeActor(val matchingEngine: MatchingEngine,
                    val settlementMechanism: ActorRef) extends ExchangeLike with BaseActor {

  def receive: Receive = {
    exchangeActorBehavior orElse baseActorBehavior
  }

}


/** Companion object for `ExchangeActor`. */
object ExchangeActor {

  def apply(matchingEngine: MatchingEngine, settlementMechanism: ActorRef): ExchangeActor = {
    new ExchangeActor(matchingEngine, settlementMechanism)
  }

  def props(matchingEngine: MatchingEngine, settlementMechanism: ActorRef): Props = {
    Props(ExchangeActor(matchingEngine, settlementMechanism))
  }

}
