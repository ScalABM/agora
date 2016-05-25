package markets.strategies

import markets.orders.Order
import markets.strategies.investment.TestRandomInvestmentStrategy
import markets.strategies.trading.TestRandomTradingStrategy
import org.apache.commons.math3.random.RandomGenerator


/** Stub implementation of the `RandomOrderIssuingStrategy` trait for testing purposes.
  *
  * @param prng an instance of a `RandomGenerator`.
  * @param config
  * @tparam T
  */
class TestRandomOrderIssuingStrategy[T <: Order](prng: RandomGenerator,
                                                 config: TestRandomOrderIssuingStrategyConfig[T])
  extends OrderIssuingStrategy[T] {

  val investmentStrategy = TestRandomInvestmentStrategy[T](prng)

  val tradingStrategy = TestRandomTradingStrategy[T](prng, config.tradingStrategyConfig)

}


object TestRandomOrderIssuingStrategy {

  def apply[T <: Order](prng: RandomGenerator,
                        config: TestRandomOrderIssuingStrategyConfig[T]): TestRandomOrderIssuingStrategy[T] = {
    new TestRandomOrderIssuingStrategy[T](prng, config)
  }

}