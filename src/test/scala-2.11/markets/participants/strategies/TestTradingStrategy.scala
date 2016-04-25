package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


/** Stub trading strategy for testing.
  *
  * @param price the price used for all orders.
  * @param quantity the quantity used for all orders.
  * @note If `price` is `None`, then an `OrderIssuer` using this strategy will generate market
  *       orders; if `price` is `Some(limitPrice)`, then an `OrderIssur` implementing this
  *       strategy will generate limit orders.
  */
case class TestTradingStrategy(price: Option[Long], quantity: Long) extends TradingStrategy {

  def askPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = price

  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long = quantity

  def bidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = price

  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = quantity

  def chooseOneOf(tickers: Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])] = {
    tickers.headOption
  }

}
