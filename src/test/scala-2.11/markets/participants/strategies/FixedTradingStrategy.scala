package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


class FixedTradingStrategy(val price: Option[Long], val quantity: Long)
  extends TradingStrategy {

  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]) = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((askPrice(ticker, tradable), askQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  def askPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long] = price

  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long = quantity

  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]) = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((bidPrice(ticker, tradable), bidQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  def bidPrice(ticker: Agent[Tick], tradable: Tradable) = price

  def bidQuantity(ticker: Agent[Tick], tradable: Tradable) = quantity

  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]) = {
    tickers.headOption
  }

}
