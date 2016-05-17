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
package markets.actors

import akka.actor.ActorRef
import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable


package object participants {

  /** Message sent to a `MarketParticipant` indicating that it should add a particular market to
    * its collections of markets and tickers.
    *
    * @param tradable
    * @param market
    * @param ticker
    */
  case class Add(tradable: Tradable, market: ActorRef, ticker: Agent[Tick])

  /** Message sent to a `MarketParticipant` indicating that it should remove a particular market
    * from tits collection of markets and tickers.
    *
    * @param tradable
    */
  case class Remove(tradable: Tradable)

  class IssueOrder

  object IssueAskOrder extends IssueOrder

  object IssueBidOrder extends IssueOrder

  object IssueOrderCancellation

}
