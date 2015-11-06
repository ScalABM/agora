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

import akka.actor.{ActorRef, Actor}
import markets.clearing.engines.MatchingEngineLike
import markets.orders.OrderLike

import scala.util.{Failure, Success}


/** Base trait for all clearing mechanisms.
  *
  * @note A `ClearingMechanismLike` actor should receive orders and fill them using its matching engine. Filled
  *       orders are then sent to a `SettlementMechanismLike` actor for further processing.
  */
trait ClearingMechanismLike {
   this: Actor =>

  /** Each clearing mechanism has a matching engine for forming prices and quantities. */
  def matchingEngine: MatchingEngineLike

  /** The settlement mechanism used to process filled orders into successful transactions. */
  def settlementMechanism: ActorRef

  def receive: Receive = {
    case order: OrderLike =>
      val result = matchingEngine.fillIncomingOrder(order)
      result match {
        case Success(filledOrders) =>
          filledOrders.foreach(filledOrder => settlementMechanism ! filledOrder)
        case Failure(ex) =>
          sender() ! ex
      }
    case _ => ???

  }

}
