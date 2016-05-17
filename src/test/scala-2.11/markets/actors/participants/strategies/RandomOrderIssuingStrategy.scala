package markets.actors.participants.strategies

import markets.actors.participants.strategies.investment.UniformRandomInvestmentStrategy
import markets.actors.participants.strategies.trading.RandomTradingStrategy
import markets.orders.Order
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.random.RandomGenerator


class RandomOrderIssuingStrategy[T <: Order](marketOrderProbability: Double,
                                             prng: RandomGenerator,
                                             priceDistribution: RealDistribution,
                                             quantityDistribution: RealDistribution)
  extends OrderIssuingStrategy[T] {

  val tradingStrategy = RandomTradingStrategy[T](marketOrderProbability, prng, priceDistribution, quantityDistribution)

  val investmentStrategy = UniformRandomInvestmentStrategy[T](prng)

}


object RandomOrderIssuingStrategy {

  def apply[T <: Order](marketOrderProbability: Double,
                        prng: RandomGenerator,
                        priceDistribution: RealDistribution,
                        quantityDistribution: RealDistribution): RandomOrderIssuingStrategy[T] = {
    new RandomOrderIssuingStrategy[T](marketOrderProbability, prng, priceDistribution, quantityDistribution)
  }

}