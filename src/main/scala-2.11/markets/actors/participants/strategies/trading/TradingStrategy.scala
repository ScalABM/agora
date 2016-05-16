package markets.actors.participants.strategies.trading

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


trait TradingStrategy[T <: Order] extends ((Tradable, Agent[Tick]) => Option[(Option[Long], Long)]) {

  def apply(tradable: Tradable, ticker: Agent[Tick]): Option[(Option[Long], Long)]

}
