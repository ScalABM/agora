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
package markets.strategies.trading

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.commons.math3.random.RandomGenerator


/** Stub implementation of the `RandomTradingStrategy` trait for testing purposes.
  *
  * @param config
  * @tparam T
  */
class TestRandomTradingStrategy[T <: Order](prng: RandomGenerator,
                                            config: TestRandomTradingStrategyConfig[T])
  extends RandomTradingStrategy[T] {

  val priceDistribution = {
    new UniformRealDistribution(prng, config.minimumPrice, config.maximumPrice)
  }

  val quantityDistribution = {
    new UniformRealDistribution(prng, config.minimumQuantity, config.maximumQuantity)
  }

  def apply(tradable: Tradable, ticker: Agent[Tick]): Option[(Option[Long], Long)] = {
    Some(specifyPrice(), specifyQuantity())
  }

  private[this] def specifyPrice(): Option[Long] = {
    if (prng.nextDouble() <= config.marketOrderProbability) {
      Some(Math.round(priceDistribution.sample()))
    } else {
      None
    }
  }

  private[this] def specifyQuantity(): Long = Math.round(quantityDistribution.sample())

}


object TestRandomTradingStrategy {

  def apply[T <: Order](prng: RandomGenerator, config: TestRandomTradingStrategyConfig[T]):
  TestRandomTradingStrategy[T] = {
    new TestRandomTradingStrategy[T](prng, config)
  }

}