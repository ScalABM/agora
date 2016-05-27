package markets

import com.typesafe.config.Config
import markets.actors.participants.issuers.TestRandomOrderIssuerConfig


case class BenchmarkSimulationConfig(seed: Long, orderIssuerConfig: TestRandomOrderIssuerConfig)


object BenchmarkSimulationConfig {

  def apply(config: Config): BenchmarkSimulationConfig = {
    val seed = config.getLong("seed")
    val configObj = config.getConfig("random-order-issuer-config")
    val randomOrderIssuerConfig = TestRandomOrderIssuerConfig(configObj)
    BenchmarkSimulationConfig(seed, randomOrderIssuerConfig)
  }

}
