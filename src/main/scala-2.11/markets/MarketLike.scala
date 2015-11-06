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

import akka.actor.{ActorRef, Actor}
import markets.orders.OrderLike
import markets.tradables.Tradable


/** Base trait for all markets.
  *
  * @note A `MarketLike` actor should directly receive `AskOrderLike` and `BidOrderLike` orders for a particular
  *       `Tradable` (filtering out any invalid orders) and then forward along all valid orders to a
  *       `ClearingMechanismLike` actor for further processing.
  */
trait MarketLike {
  this: Actor =>

  /** The mechanism used to determine prices and quantities. */
  def clearingMechanism: ActorRef

  /** The object being traded on the market. */
  def tradable: Tradable

  def receive: Receive = {
    case order: OrderLike if order.tradable == tradable =>
      clearingMechanism forward order
      sender() ! OrderAccepted
    case order: OrderLike if !(order.tradable == tradable) =>
      sender() ! OrderRejected
  }

}



