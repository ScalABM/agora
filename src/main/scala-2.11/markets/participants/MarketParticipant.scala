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
package markets.participants

import akka.actor.ActorRef
import akka.agent.Agent

import markets.tickers.Tick
import markets.{Add, Remove, StackableActor}
import markets.tradables.Tradable


/** Base Trait for all market participants. */
trait MarketParticipant extends StackableActor {

  var markets: Map[Tradable, ActorRef]

  var tickers: Map[Tradable, Agent[Tick]]

  override def receive: Receive = {
    case Add(market, ticker, _, tradable, _) =>
      markets += (tradable -> market)
      tickers += (tradable -> ticker)
    case Remove(_, tradable, _) =>
      markets -= tradable
      tickers -= tradable
    case message =>
      super.receive(message)
  }

}
