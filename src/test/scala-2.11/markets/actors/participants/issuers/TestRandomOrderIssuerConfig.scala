package markets.actors.participants.issuers

import com.typesafe.config.Config
import markets.orders.{AskOrder, BidOrder}
import markets.strategies.TestRandomOrderIssuingStrategyConfig


case class TestRandomOrderIssuerConfig(askOrderIssuingStrategyConfig: TestRandomOrderIssuingStrategyConfig[AskOrder],
                                       bidOrderIssuingStrategyConfig: TestRandomOrderIssuingStrategyConfig[BidOrder])


object TestRandomOrderIssuerConfig {

  def apply(config: Config): TestRandomOrderIssuerConfig = {
    val askOrderIssuingStrategyConfig = {
      val configObj = config.getConfig("ask-order-issuing-strategy-config")
      TestRandomOrderIssuingStrategyConfig[AskOrder](configObj)
    }
    val bidOrderIssuingStrategyConfig = {
      val configObj = config.getConfig("bid-order-issuing-strategy-config")
      TestRandomOrderIssuingStrategyConfig[BidOrder](configObj)
    }
    TestRandomOrderIssuerConfig(askOrderIssuingStrategyConfig, bidOrderIssuingStrategyConfig)
  }

}