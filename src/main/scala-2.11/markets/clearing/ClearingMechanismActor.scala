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

import akka.actor.{Props, ActorRef}

import markets.BaseActor
import markets.clearing.engines.MatchingEngineLike
import markets.orders.Order


/** Actor for modeling market clearing mechanisms.
  *
  * A `ClearingMechanismActor` actor should receive orders and match them using its matching
  * engine. Matched orders are then used to generate `Fills` which are then sent to a
  * `SettlementMechanismLike` actor for further processing.
  * @param matchingEngine A `ClearingMechanismActor` has a matching engine for forming prices and
  *                       quantities
  * @param settlementMechanism A `ClearingMechanismActor` has access to some settlement mechanism
  *                            that it uses to process matches into successful transactions.
  */
class ClearingMechanismActor(val matchingEngine: MatchingEngineLike,
                             val settlementMechanism: ActorRef) extends BaseActor
  with ClearingMechanismLike {

  def clearingMechanismBehavior: Receive = {
    case order: Order =>
      val result = matchingEngine.findMatch(order)
      result match {
        case Some(matchedOrders) =>
          val timestamp = context.system.uptime
          matchedOrders.foreach { m => settlementMechanism ! Fill(m, timestamp) }
        case None =>  // @todo notify sender that no matches were generated!
      }
  }

  def receive: Receive = {
    clearingMechanismBehavior orElse baseActorBehavior
  }

}


/** Companion object for `ClearingMechanismActor`. */
object ClearingMechanismActor {

  def props(matchingEngine: MatchingEngineLike, settlementMechanism: ActorRef): Props = {
    Props(new ClearingMechanismActor(matchingEngine, settlementMechanism))
  }

}