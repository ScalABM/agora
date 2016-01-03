package markets.participants

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


class TestLimitOrderTradingStrategy extends LimitOrderTradingStrategy {

  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Long, Long, Tradable)] = {
    limitOrderStrategy(tickers)
  }

  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Long, Long, Tradable)] = {
    limitOrderStrategy(tickers)
  }

  private[this] def chooseOneOf(tradables: Iterable[Tradable]) = {
    tradables.headOption
  }

  private[this] def limitOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]) = {
    chooseOneOf(tickers.keys) match {
      case Some(tradable) =>
        Some((1L, 1L, tradable))
      case None =>
        None
    }
  }

}
