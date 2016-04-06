package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


class FixedTradingStrategy(price: Option[Long], quantity: Long) extends TradingStrategy {

  def askPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = price

  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long = quantity

  def bidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = price

  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = quantity

  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])] = {
    tickers.headOption
  }

}
