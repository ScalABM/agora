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


/** Actor for modeling markets.
  *
  * @param matchingEngine
  * @param settlementMechanism
  * @param tradable The object being traded on the market.
  */
case class MarketActor(matchingEngine: MatchingEngine,
                       settlementMechanism: ActorRef,
                       ticker: Agent[immutable.Seq[Tick]],
                       tradable: Tradable)
  extends StackableActor {

  wrappedBecome(marketActorBehavior)

  def marketActorBehavior: Receive = {
    case order: Order =>
      if(order.tradable == tradable) {
        matchingEngine.findMatch(order) match {
          case Some(matchings) =>
            matchings.foreach { matching =>
              val fill = Fill.fromMatching(matching, timestamp(), uuid())
              val tick = Tick.fromFill(fill)
              ticker.send( tick +: _ ) // SIDE EFFECT!
              settlementMechanism tell(fill, self)
            }
          case None => // @todo notify sender that no matches were generated?
        }
      } else {
        sender() tell(Rejected(order, timestamp(), uuid()), self)
      }
    case Cancel(order, _, _) =>
      val result = matchingEngine.remove(order)
      result match {
        case Some(residualOrder) => // Case notify order successfully canceled
          sender() tell(Canceled(residualOrder, timestamp(), uuid()), self)
        case None =>  // @todo notify sender that order was not canceled?
      }
  }

}


/** Companion object for the `MarketActor`. */
object MarketActor {

  def props(matchingEngine: MatchingEngine,
            settlementMechanism: ActorRef,
            ticker: Agent[immutable.Seq[Tick]],
            tradable: Tradable): Props = {
    Props(MarketActor(matchingEngine, settlementMechanism, ticker, tradable))
  }

}
