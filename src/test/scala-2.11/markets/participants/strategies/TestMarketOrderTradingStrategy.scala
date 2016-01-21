package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.{immutable, mutable}


class TestMarketOrderTradingStrategy extends MarketOrderTradingStrategy {

  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[immutable.Seq[Tick]]]): Option[(Long, Tradable)] = {
    marketOrderStrategy(tickers)
  }

  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[immutable.Seq[Tick]]]): Option[(Long, Tradable)] = {
    marketOrderStrategy(tickers)
  }

  private[this] def chooseOneOf(tradables: Iterable[Tradable]) = {
    tradables.headOption
  }

  private[this] def marketOrderStrategy(tickers: mutable.Map[Tradable, Agent[immutable.Seq[Tick]]]) = {
    chooseOneOf(tickers.keys) match {
      case Some(tradable) =>
        Some((1L, tradable))
      case None =>
        None
    }
  }

}
