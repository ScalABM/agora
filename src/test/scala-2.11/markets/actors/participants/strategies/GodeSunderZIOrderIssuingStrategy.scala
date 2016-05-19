package markets.actors.participants.strategies

import markets.actors.participants.strategies.investment.UniformRandomInvestmentStrategy
import markets.actors.participants.strategies.trading.GodeSunderZITradingStrategy
import markets.orders.Order
import org.apache.commons.math3.random.RandomGenerator


class GodeSunderZIOrderIssuingStrategy[T <: Order](minimumPrice: Long,
                                                   maximumPrice: Long,
                                                   quantity: Long,
                                                   prng: RandomGenerator)
  extends OrderIssuingStrategy[T] {

  val investmentStrategy = UniformRandomInvestmentStrategy[T](prng)

  val tradingStrategy = GodeSunderZITradingStrategy[T](minimumPrice, maximumPrice, quantity, prng)

}


object GodeSunderZIOrderIssuingStrategy {

  def apply[T <: Order](minimumPrice: Long,
                        maximumPrice: Long,
                        quantity: Long,
                        prng: RandomGenerator): GodeSunderZIOrderIssuingStrategy[T] = {
    new GodeSunderZIOrderIssuingStrategy[T](minimumPrice, maximumPrice, quantity, prng)
  }

  def apply[T <: Order](config: GodeSunderZIOrderIssuingStrategyConfig,
                        prng: RandomGenerator): GodeSunderZIOrderIssuingStrategy[T] = {
    val minimumPrice = config.minimumPrice
    val maximumPrice = config.maximumPrice
    val quantity = config.quantity
    new GodeSunderZIOrderIssuingStrategy[T](minimumPrice, maximumPrice, quantity, prng)
  }

}