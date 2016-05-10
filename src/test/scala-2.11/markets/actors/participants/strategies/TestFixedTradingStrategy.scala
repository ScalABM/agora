package markets.actors.participants.strategies

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
case class TestFixedTradingStrategy(price: Option[Long], quantity: Long) extends
  FixedTradingStrategy {

  val askPrice = price

  val askQuantity = quantity

  val bidPrice = price

  val bidQuantity = quantity

  def chooseOneOf(tickers: Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])] = {
    tickers.headOption
  }

}
