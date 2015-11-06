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
package exchanges

import akka.actor.{Props, Actor}
import markets.MarketLike
import markets.clearing.ClearingMechanismLike
import markets.orders.OrderLike
import markets.settlement.SettlementMechanismLike
import markets.tradables.Tradable

import scala.collection.immutable


/** Base trait for all `ExchangeLike` actors.
  *
  * @note An `ExchangeLike` represents a collection of `MarketLike` actors that share a common settlement
  *       mechanism.
  */
trait ExchangeLike {
  this: Actor =>

  def clearingMechanisms: immutable.Seq[ClearingMechanismLike]

  def tradables: immutable.Seq[Tradable]

  /* Settlement mechanism is a child of the ExchangeLike. */
  val settlementMechanism = ???

  /* Create a market actor for each security in tickers. */
  val markets = ???

  def receive: Receive = {
    case order: OrderLike => ???
    case AddMarket(clearingMechanism, tradable) =>  // add a market to the exchange
    case RemoveMarket(tradable) =>  // remove a market from the exchange
    case _ => ???
  }

  case class AddMarket(clearingMechanism: ClearingMechanismLike, tradable: Tradable)

  case class RemoveMarket(tradable: Tradable)

}
