package markets.actors.participants.strategies.trading

import markets.orders.Order
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.random.RandomGenerator


trait RandomQuantity[T <: Order] {
  this: TradingStrategy[T] =>

  def quantityDistribution: RealDistribution

  def prng: RandomGenerator

}
