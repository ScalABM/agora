package markets.actors.participants.strategies

import com.typesafe.config.Config


case class RandomTradingStrategyConfig(config: Config) {

  val marketOrderProbability = config.getDouble("market-order-probability")

  val minAskPrice = config.getLong("minimum-ask-price")

  val maxAskPrice = config.getLong("maximum-ask-price")

  val minAskQuantity = config.getLong("minimum-ask-quantity")

  val maxAskQuantity = config.getLong("maximum-ask-quantity")

  val minBidPrice = config.getLong("minimum-bid-price")

  val maxBidPrice = config.getLong("maximum-bid-price")

  val minBidQuantity = config.getLong("minimum-bid-quantity")

  val maxBidQuantity = config.getLong("maximum-bid-quantity")

}
