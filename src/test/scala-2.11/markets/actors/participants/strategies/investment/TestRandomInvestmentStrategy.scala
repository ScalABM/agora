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


class TestRandomInvestmentStrategy[T <: Order](val prng: RandomGenerator)
  extends InvestmentStrategy[T] with SamplingDistribution[T] {

  def pmf(tradables: Set[Tradable]): util.List[Pair[Tradable, Double]] = {
    val numberTradables = tradables.size
    val selectionProbability = new lang.Double(1 / numberTradables)
    (for (tradable <- tradables) yield new Pair(tradable, selectionProbability)).toBuffer.asJava
  }

  def apply(information: Map[Tradable, Agent[Tick]]): Option[Tradable] = {
    if (information.isEmpty) None else Some(samplingDistribution(information.keySet).sample())
  }

}

