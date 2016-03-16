package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


class FixedMarketOrderTradingStrategy(val quantity: Long) extends MarketOrderTradingStrategy {

  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((askQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    quantity
  }

  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Long, Tradable)] = {
    chooseOneOf(tickers) match {
      case Some((tradable, ticker)) =>
        Some((bidQuantity(ticker, tradable), tradable))
      case None =>
        None
    }
  }

  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long = {
    quantity
  }

  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]) = {
    tickers.headOption
  }

}

