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
package markets.strategies.investment

import akka.agent.Agent

import java.{lang, util}

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.util.Pair

import scala.collection.JavaConverters._


/** Stub implementation of the `RandomInvestmentStrategy` trait for testing purposes.
  *
  * @param prng
  * @tparam T
  */
class TestRandomInvestmentStrategy[T <: Order](prng: RandomGenerator)
  extends RandomInvestmentStrategy[T] {

  def apply(information: Map[Tradable, Agent[Tick]]): Option[Tradable] = {
    val pmf = probabilityMassFunction(information)
    val tradablesDistribution = new EnumeratedDistribution[Tradable](prng, pmf)
    if (information.isEmpty) None else Some(tradablesDistribution.sample())
  }

  def probabilityMassFunction(information: Map[Tradable, Agent[Tick]]): util.List[Pair[Tradable, lang.Double]] = {
    val selectionProbability = new lang.Double(1.0 / information.size)  // Float division!
    information.keys.map(tradable => new Pair(tradable, selectionProbability)).toBuffer.asJava
  }

}


object TestRandomInvestmentStrategy {

  def apply[T <: Order](prng: RandomGenerator): TestRandomInvestmentStrategy[T] = {
    new TestRandomInvestmentStrategy[T](prng)
  }

}

