package markets.actors.participants.strategies.trading

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.random.RandomGenerator


trait RandomQuantity {
  this: TradingStrategy =>

  /** The underlying distribution from which quantities for ask orders are drawn. */
  val askQuantityDistribution: RealDistribution

  /** The underlying distribution from which quantities for bid orders are drawn. */
  val bidQuantityDistribution: RealDistribution

  val prng: RandomGenerator

  /** Rule used to specify a quantity for an [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def getAskQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    Math.round(askQuantityDistribution.sample())
  }

  /** Rule used to specify a quantity for an [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def getBidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    Math.round(bidQuantityDistribution.sample())
  }

}
