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

import akka.actor.{Actor, ActorRef, Props}

import markets.NotUnderstood
import markets.clearing.engines.MatchingEngineLike
import markets.orders.OrderLike

import scala.util.Failure


/** Actor for modeling market clearing mechanisms.
  *
  * A `ClearingMechanismActor` actor should receive orders and fill them using its matching
  * engine. Filled
  * orders are then sent to a `SettlementMechanismLike` actor for further processing.
  * @param matchingEngine A `ClearingMechanismActor` has a matching engine for forming prices and
  *                       quantities
  * @param settlementMechanism A `ClearingMechanismActor` has access to some settlement mechanism
  *                            that it uses to process filled orders into successful transactions.
  */
class ClearingMechanismActor(val matchingEngine: MatchingEngineLike,
                             val settlementMechanism: ActorRef) extends Actor {

  def receive: Receive = {
    case order: OrderLike =>
      val result = matchingEngine.fillIncomingOrder(order)
      result match {
        case Some(filledOrders) =>
          filledOrders.foreach(filledOrder => settlementMechanism ! filledOrder)
        case None =>
          sender() ! Failure(throw new Exception())  // @todo fix this!
      }
    case _ =>
      sender() ! NotUnderstood(???)

  }

}


/** Companion object for `ClearingMechanismActor`. */
object ClearingMechanismActor {

  def apply(matchingEngine: MatchingEngineLike,
            settlementMechanism: ActorRef): ClearingMechanismActor = {
    new ClearingMechanismActor(matchingEngine, settlementMechanism)
  }

  def props(matchingEngine: MatchingEngineLike, settlementMechanism: ActorRef): Props = {
    Props(new ClearingMechanismActor(matchingEngine, settlementMechanism))
  }

}