package markets.strategies

import com.typesafe.config.Config
import markets.orders.Order
import markets.strategies.investment.TestRandomInvestmentStrategyConfig
import markets.strategies.trading.TestRandomTradingStrategyConfig


case class TestRandomOrderIssuingStrategyConfig[T <: Order](investmentStrategyConfig: TestRandomInvestmentStrategyConfig[T],
                                                            tradingStrategyConfig: TestRandomTradingStrategyConfig[T])


object TestRandomOrderIssuingStrategyConfig {

  def apply[T <: Order](config: Config): TestRandomOrderIssuingStrategyConfig[T] = {
    val investmentStrategyConfig = {
      TestRandomInvestmentStrategyConfig[T](config.getConfig("investment-strategy-config"))
    }
    val tradingStrategyConfig = {
      TestRandomTradingStrategyConfig[T](config.getConfig("trading-strategy-config"))
    }
    TestRandomOrderIssuingStrategyConfig[T](investmentStrategyConfig, tradingStrategyConfig)
  }

}