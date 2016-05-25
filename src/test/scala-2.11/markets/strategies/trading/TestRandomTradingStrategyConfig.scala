package markets.strategies.trading

import com.typesafe.config.Config
import markets.orders.Order


case class TestRandomTradingStrategyConfig[T <: Order](marketOrderProbability: Double,
                                                       minimumPrice: Long,
                                                       maximumPrice: Long,
                                                       minimumQuantity: Long,
                                                       maximumQuantity: Long) {

  require(minimumPrice <= maximumPrice, "Min price must be less than (or equal to) max price.")
  require(minimumQuantity <= maximumQuantity, "Min quantity must be less than (or equal to) max quantity.")
  require(0 <= marketOrderProbability, "Probability of market order must be non-negative.")
  require(marketOrderProbability <= 1, "Probability of market order must be less than (or equal to) 1.")

}


object TestRandomTradingStrategyConfig {

  def apply[T <: Order](config: Config): TestRandomTradingStrategyConfig[T] = {
    TestRandomTradingStrategyConfig[T](config.getDouble("market-order-probability"),
      config.getLong("minimum-price"), config.getLong("maximum-price"),
      config.getLong("minimum-quantity"), config.getLong("maximum-quantity"))
  }

}
