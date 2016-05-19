package markets.actors.participants.strategies

import com.typesafe.config.Config


case class GodeSunderZIOrderIssuingStrategyConfig(config: Config) {

  val minimumPrice = config.getLong("minimum-price")

  val maximumPrice = config.getLong("maximum-price")

  val quantity = config.getLong("quantity")

}
