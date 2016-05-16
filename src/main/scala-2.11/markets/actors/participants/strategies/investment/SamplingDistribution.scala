package markets.actors.participants.strategies.investment

import java.{lang, util}

import markets.orders.Order
import markets.tradables.Tradable
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.util.Pair


trait SamplingDistribution[T <: Order] {
  this: InvestmentStrategy[T] =>

  def prng: RandomGenerator

  def pmf(tradables: Set[Tradable]): util.List[Pair[Tradable, lang.Double]]

  protected def samplingDistribution(tradables: Set[Tradable]): EnumeratedDistribution[Tradable] = {
    new EnumeratedDistribution[Tradable](prng, pmf(tradables))
  }

}

