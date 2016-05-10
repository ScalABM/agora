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
package markets.actors.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable


trait FixedQuantities {
  this: TradingStrategy =>

  /** The fixed quantity for an `AskOrder`. */
  val askQuantity: Long

  /** The fixed quantity for a `BidOrder`. */
  val bidQuantity: Long

  /** Returns the fixed quantity for an `AskOrder`.
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def chooseAskQuantity(ticker: Agent[Tick], tradable: Tradable): Long = askQuantity

  /** Returns the fixed quantity for a `BidOrder`.
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def chooseBidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = bidQuantity

}
