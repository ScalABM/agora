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
package markets.clearing

import akka.actor.ActorRef
import akka.agent.Agent

import markets.{BaseActor, Cancel, Canceled}
import markets.clearing.engines.MatchingEngine
import markets.orders.Order
import markets.tickers.Tick


/** Mixin Trait providing ClearingMechanismLike behavior to some BaseActor. */
trait ClearingMechanismLike {
  this: BaseActor =>

  def matchingEngine: MatchingEngine

  def settlementMechanism: ActorRef

  def ticker: Agent[Tick]

  def clearingMechanismBehavior: Receive = {
    case order: Order =>
      val result = matchingEngine.findMatch(order)
      result match {
        case Some(matchings) =>
          matchings.foreach { matching =>
            val fill = Fill.fromMatching(matching, timestamp(), uuid())
            val tick = Tick.fromFill(fill)
            ticker.send(tick)  // SIDE EFFECT!
            settlementMechanism ! fill
          }
        case None =>  // @todo notify sender that no matches were generated?
      }
    case Cancel(order, _, _) =>
      val result = matchingEngine.remove(order)
      result match {
        case Some(residualOrder) => // Case notify order successfully canceled
          sender() ! Canceled(residualOrder, timestamp(), uuid())
        case None =>  // @todo notify sender that order was not canceled!
      }
  }

}
