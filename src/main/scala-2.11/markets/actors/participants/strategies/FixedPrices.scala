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


trait FixedPrices {
  this: TradingStrategy =>

  /** The desired price for an `AskOrder`. */
  val askPrice: Option[Long]

  /** The desired price for a `BidOrder`. */
  val bidPrice: Option[Long]

  /** Rule used to generate a price for an order to sell some tradable.
    *
    * @param ticker   an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *                 `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def chooseAskPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = askPrice

  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker   an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *                 `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def chooseBidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = bidPrice

}
