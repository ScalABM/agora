package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


class FixedLimitOrderTradingStrategy(val price: Long, val quantity: Long)
  extends LimitOrderTradingStrategy {

  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Long, Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((askPrice(ticker, tradable), askQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  def askPrice(ticker: Agent[Tick], tradable: Tradable): Long = {
    price
  }

  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    quantity
  }

  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Long, Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((bidPrice(ticker, tradable), bidQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  def bidPrice(ticker: Agent[Tick], tradable: Tradable): Long = {
    price
  }

  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    quantity
  }

  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]) = {
    tickers.headOption
  }

}
