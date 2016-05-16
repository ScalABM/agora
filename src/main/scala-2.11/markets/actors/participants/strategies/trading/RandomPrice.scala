package markets.actors.participants.strategies.trading

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.random.RandomGenerator


trait RandomPrice {
  this: TradingStrategy =>

  /** The underlying distribution from which prices for ask orders are drawn. */
  val askPriceDistribution: RealDistribution

  /** The underlying distribution from which prices for bid orders are drawn. */
  val bidPriceDistribution: RealDistribution

  def marketOrderProbability: Double

  def prng: RandomGenerator

  /** Rule used to specify a price for an [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def getAskPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble() <= marketOrderProbability) {
      val price = Math.round(askPriceDistribution.sample())
      Some(price)
    } else {
      None
    }
  }

  /** Rule used to specify a price for an [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def getBidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble() <= marketOrderProbability) {
      val price = Math.round(bidPriceDistribution.sample())
      Some(price)
    } else {
      None
    }
  }

}
