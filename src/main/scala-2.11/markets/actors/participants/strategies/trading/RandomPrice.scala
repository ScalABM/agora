package markets.actors.participants.strategies.trading

import markets.orders.Order
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.random.RandomGenerator


trait RandomPrice[T <: Order] {
  this: TradingStrategy[T] =>

  def priceDistribution: RealDistribution

  def prng: RandomGenerator

}
