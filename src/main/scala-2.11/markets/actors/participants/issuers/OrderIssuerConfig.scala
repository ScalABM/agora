package markets.actors.participants.issuers

import com.typesafe.config.Config


trait OrderIssuerConfig {

  def config: Config

  lazy val askOrderIssuingStrategyConfig = config.getConfig("ask-order-issuing-strategy")

  lazy val bidOrderIssuingStrategyConfig = config.getConfig("bid-order-issuing-strategy")

}
