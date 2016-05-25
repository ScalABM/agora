package markets.strategies.investment

import com.typesafe.config.Config
import markets.orders.Order


case class TestRandomInvestmentStrategyConfig[T]()


object TestRandomInvestmentStrategyConfig {

  def apply[T <: Order](config: Config): TestRandomInvestmentStrategyConfig[T] = {
    TestRandomInvestmentStrategyConfig[T]()
  }

}