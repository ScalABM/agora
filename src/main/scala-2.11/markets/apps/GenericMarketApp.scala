package markets.apps

import com.typesafe.config.Config
import markets.tradables.Tradable

import scala.util.Random


trait GenericMarketApp {

  def config: Config

  val seed = config.getLong("simulation.seed")
  val prng = new Random(seed)

  /* Setup the tradables. */
  val numberTradables = config.getInt("simulation.tradables.number")
  val tradables = for (i <- 1 to numberTradables) yield {
    val symbolLength = config.getInt("simulation.tradables.symbol-length")
    val symbol = prng.nextString(symbolLength)
    val tick = config.getInt("simulation.tradables.tick")
    Tradable(symbol, tick)
  }

}
