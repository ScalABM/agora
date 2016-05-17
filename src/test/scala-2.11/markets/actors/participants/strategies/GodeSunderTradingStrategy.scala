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

import markets.actors.participants.strategies.trading.{ConstantQuantity, RandomPrice, TradingStrategy}
import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.commons.math3.random.RandomGenerator


/** Class implementing the Zero Intelligence (ZI) trading strategy from Gode-Sunder (JPE, 1996).
  *
  * @param minPrice
  * @param maxPrice
  * @param prng
  */
case class GodeSunderTradingStrategy[T <: Order](minPrice: Long,
                                                 maxPrice: Long,
                                                 quantity: Long,
                                                 prng: RandomGenerator)
  extends TradingStrategy[T]
  with RandomPrice[T]
  with ConstantQuantity[T] {

  val priceDistribution = new UniformRealDistribution(prng, minPrice, maxPrice)

  def apply(tradable: Tradable, ticker: Agent[Tick]): Option[(Option[Long], Long)] = {
    val price = Some(Math.round(priceDistribution.sample()))
    Some(price, quantity)
  }

}
