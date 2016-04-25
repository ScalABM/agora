package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.util.Random


case class TestRandomTradingStrategy(config: RandomTradingStrategyConfig, prng: Random)
  extends RandomTradingStrategy {

  import TestRandomTradingStrategy._

  /** Rule used to generate a price for an order to sell some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def askPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble < config.marketOrderProbability) {
      None
    } else {
      Some(nextLong(prng, config.minAskPrice, config.maxAskPrice))
    }
  }

  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def bidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble < config.marketOrderProbability) {
      None
    } else {
      Some(nextLong(prng, config.minBidPrice, config.maxBidPrice))
    }
  }

  /** Rule used to select some tradable from a collection of tradables.
    *
    * @param tickers
    * @return
    */
  def chooseOneOf(tickers: Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])] = {
    val tradables = tickers.toArray
    if (tickers.isEmpty) None else Some(tradables(prng.nextInt(tickers.size)))

  }

  /** Rule used to generate a quantity for an order to sell some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    nextLong(prng, config.minAskQuantity, config.maxAskQuantity)
  }

  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    nextLong(prng, config.minBidQuantity, config.maxBidQuantity)
  }

}


object TestRandomTradingStrategy {

  def nextLong(prng: Random, lower: Long, upper: Long): Long = {
    (lower + (upper - lower) * prng.nextDouble()).toLong
  }

}
