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

import markets.engines.mutable.MutableTreeSetCDAMatchingEngine
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable

/** Actor for modeling markets.
  *
  * @param askOrdering
  * @param bidOrdering
  * @param settlementMechanism
  * @param ticker
  * @param tradable
  */
case class MutableTreeSetCDAMarketActor(askOrdering: Ordering[AskOrder],
                                        bidOrdering: Ordering[BidOrder],
                                        initialPrice: Long,
                                        settlementMechanism: ActorRef,
                                        ticker: Agent[Tick],
                                        tradable: Tradable)
  extends GenericCDAMarketActor[mutable.TreeSet[AskOrder], mutable.TreeSet[BidOrder]] {

  val matchingEngine = new MutableTreeSetCDAMatchingEngine(askOrdering, bidOrdering, initialPrice)

}


/** Companion object for the `MarketActor`. */
object MutableTreeSetCDAMarketActor {

  def props(askOrdering: Ordering[AskOrder],
            bidOrdering: Ordering[BidOrder],
            initialPrice: Long,
            settlementMechanism: ActorRef,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(MutableTreeSetCDAMarketActor(askOrdering, bidOrdering, initialPrice, settlementMechanism, ticker, tradable))
  }

}
