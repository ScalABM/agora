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

import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.Fill
import markets.engines.immutable.ImmutableTreeSetCDAMatchingEngine
import markets.orders.{AskOrder, BidOrder, Order}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.immutable.TreeSet


/** Actor for modeling markets.
  *
  * @param askOrdering
  * @param bidOrdering
  * @param settlementMechanism
  * @param ticker
  * @param tradable
  */
case class ImmutableTreeSetCDAMarketActor(askOrdering: Ordering[AskOrder],
                                          bidOrdering: Ordering[BidOrder],
                                          initialPrice: Long,
                                          settlementMechanism: ActorRef,
                                          ticker: Agent[Tick],
                                          tradable: Tradable)
  extends GenericCDAMarketActor[TreeSet[AskOrder], TreeSet[BidOrder]] {

  val matchingEngine = new ImmutableTreeSetCDAMatchingEngine(askOrdering, bidOrdering, initialPrice)

}


/** Companion object for the `MarketActor`. */
object ImmutableTreeSetCDAMarketActor {

  def props(askOrdering: Ordering[AskOrder],
            bidOrdering: Ordering[BidOrder],
            initialPrice: Long,
            settlementMechanism: ActorRef,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(ImmutableTreeSetCDAMarketActor(askOrdering, bidOrdering, initialPrice, settlementMechanism, ticker, tradable))
  }

}
