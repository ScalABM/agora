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


/** Base trait for all trading strategies. */
trait TradingStrategy {

  /** Strategy for generating a [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param tickers
    * @return a tuple consisting of a price (optional), quantity, and tradable.
    * @note If the first element of the returned tuple is `Some(price)`, then the strategy will
    *       be used to generate a [[markets.orders.limit.LimitAskOrder `LimitAskOrder`]];
    *       otherwise, if the first element of the returned tuple is `None`, then the strategy
    *       will be used to generate a [[markets.orders.market.MarketAskOrder `MarketAskOrder`]].
    */
  def askOrderStrategy(tickers: Map[Tradable, Agent[Tick]]): Option[(Option[Long], Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        val price = chooseAskPrice(ticker, tradable)
        val quantity = chooseAskQuantity(ticker, tradable)
        Some((price, quantity, tradable))
      case None =>
        None
    }
  }

  /** Rule used to specify a price for an [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def chooseAskPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long]

  /** Rule used to specify a quantity for an [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def chooseAskQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  /** Strategy for generating orders a [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param tickers
    * @return a tuple consisting of a price (optional), quantity, and tradable.
    * @note If the first element of the returned tuple is `Some(price)`, then the strategy will
    *       be used to generate a [[markets.orders.limit.LimitBidOrder `LimitBidOrder`]];
    *       otherwise, if the first element of the returned tuple is `None`, then the strategy
    *       will be used to generate a [[markets.orders.market.MarketBidOrder `MarketBidOrder`]].
    */
  def bidOrderStrategy(tickers: Map[Tradable, Agent[Tick]]): Option[(Option[Long], Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((chooseBidPrice(ticker, tradable), chooseBidQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  /** Rule used to specify a price for a [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def chooseBidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long]

  /** Rule used to specify a quantity for a [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def chooseBidQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  /** Rule used to select some `ticker` from a collection of `tickers`.
    *
    * @param tickers
    * @return `None` if `tickers` is empty; otherwise some key-value pair from `tickers`.
    */
  def chooseOneOf(tickers: Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])]

}
