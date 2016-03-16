package markets.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


trait TradingStrategy[T] {

  def askOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[T]

  def askQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  def bidOrderStrategy(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[T]

  def bidQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  def chooseOneOf(tickers: mutable.Map[Tradable, Agent[Tick]]): Option[(Tradable, Agent[Tick])]

}
