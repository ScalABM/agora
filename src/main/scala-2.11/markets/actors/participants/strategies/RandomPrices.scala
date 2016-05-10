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
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.random.RandomGenerator


trait RandomPrices {
  this: TradingStrategy =>

  /** The underlying distribution from which prices for ask orders are drawn. */
  val askPriceDistribution: RealDistribution

  /** The underlying distribution from which prices for bid orders are drawn. */
  val bidPriceDistribution: RealDistribution

  def marketOrderProbability: Double

  def prng: RandomGenerator

  /** Generates a randomly chosen price for an order to sell some tradable.
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def chooseAskPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble() <= marketOrderProbability) {
      val price = Math.round(askPriceDistribution.sample())
      Some(price)
    } else {
      None
    }
  }

  /** Generates a randomly chosen price for an order to buy some tradable.
    *
    * @param ticker   an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *                 `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def chooseBidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble() <= marketOrderProbability) {
      val price = Math.round(bidPriceDistribution.sample())
      Some(price)
    } else {
      None
    }
  }

}
