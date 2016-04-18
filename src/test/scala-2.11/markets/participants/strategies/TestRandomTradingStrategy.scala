package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Random


class TestRandomTradingStrategy(val prng: Random)
  extends RandomTradingStrategy {

  import TestRandomTradingStrategy._

  /** Rule used to generate a price for an order to sell some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def askPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble < 0.5) {
      None
    } else {
      Some(nextLong(prng, 1, 200))
    }
  }

  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def bidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = {
    if (prng.nextDouble < 0.5) {
      None
    } else {
      Some(nextLong(prng, 1, 200))
    }
  }

  /** Rule used to select some tradable from a collection of tradables.
    *
    * @param tickers
    * @return
    */
  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])] = {
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
    nextLong(prng, 1, 1)
  }

  /** Rule used to generate a price for an order to buy some tradable.
    *
    * @param ticker
    * @param tradable
    * @return
    */
  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    nextLong(prng, 1, 1)
  }

}


object TestRandomTradingStrategy {

  def nextLong(prng: Random, lower: Long, upper: Long): Long = {
    (lower + (upper - lower) * prng.nextDouble()).toLong
  }

}
