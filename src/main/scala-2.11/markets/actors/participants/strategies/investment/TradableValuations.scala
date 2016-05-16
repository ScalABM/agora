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
package markets.actors.participants.strategies.investment

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


trait TradableValuations[T <: Order] {
  this: InvestmentStrategy[T] =>

  def valuations: Map[Tradable, Long]

  /** Collection of tickers for over-valued tradables.
    *
    * @param information the collection of relevant information for making investment decision.
    * @return the subset of information relevant for those tradables that are over-valued given
    *         current `valuations`.
    */
  protected final def overValuedTradables(information: Map[Tradable, Agent[Tick]]) = {
    information.filter {
      case (tradable, ticker) => valuations(tradable) <= ticker.get.price
    }
  }

  /** Collection of tickers for under-valued tradables.
    *
    * @param information the collection of relevant information for making investment decision.
    * @return the subset of information relevant for those tradables that are under-valued given
    *         current `valuations`.
    */
  protected final def underValuedTradables(information: Map[Tradable, Agent[Tick]]) = {
    information.filter {
      case (tradable, ticker) => valuations(tradable) >= ticker.get.price
    }
  }

}
