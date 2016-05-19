package markets.actors.participants

import com.typesafe.config.Config


case class GodeSunderZIOrderIssuerConfig(config: Config) {

  val seed = {
    if (config.getString("seed").equalsIgnoreCase("None")) {
      None
    } else {
      Some(config.getLong("seed"))
    }
  }

  val askOrderIssuingStrategyConfig = config.getConfig("ask-order-issuing-strategy")

  val bidOrderIssuingStrategyConfig = config.getConfig("bid-order-issuing-strategy")

}
