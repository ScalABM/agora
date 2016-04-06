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
package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


trait TradingStrategy {

  /** Strategy for generating orders to sell some tradable.
    *
    * @param tickers
    * @return
    */
  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Option[Long], Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((askPrice(ticker, tradable), askQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  /** Rule used to generate a price for an order to sell some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def askPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long]

  /** Rule used to generate a quantity for an order to sell some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  /** Strategy for generating orders to buy some tradable.
    *
    * @param tickers
    * @return
    */
  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Option[Long], Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((bidPrice(ticker, tradable), bidQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }
  
  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def bidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long]

  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  /** Rule used to select some tradable from a collection of tradables.
    *
    * @param tickers
    * @return
    */
  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])]

}
