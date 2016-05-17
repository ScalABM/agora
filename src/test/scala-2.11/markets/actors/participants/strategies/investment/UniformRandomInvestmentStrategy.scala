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

import java.lang.Double
import java.{lang, util}

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.util.Pair

import scala.collection.JavaConverters._


class UniformRandomInvestmentStrategy[T <: Order](val prng: RandomGenerator)
  extends InvestmentStrategy[T]
  with RandomTradable[T] {

  def pmf(tradables: Set[Tradable]): util.List[Pair[Tradable, Double]] = {
    val numberTradables = tradables.size
    val selectionProbability = new lang.Double(1.0 / numberTradables)  // Float division!
    (for (tradable <- tradables) yield new Pair(tradable, selectionProbability)).toBuffer.asJava
  }

  def apply(information: Map[Tradable, Agent[Tick]]): Option[Tradable] = {
    if (information.isEmpty) None else Some(samplingDistribution(information.keySet).sample())
  }

}


object UniformRandomInvestmentStrategy {

  def apply[T <: Order](prng: RandomGenerator): UniformRandomInvestmentStrategy[T] = {
    new UniformRandomInvestmentStrategy[T](prng)
  }

}
