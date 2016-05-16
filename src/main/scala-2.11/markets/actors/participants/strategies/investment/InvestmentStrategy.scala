package markets.actors.participants.strategies.investment

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


trait InvestmentStrategy[T <: Order] extends (Map[Tradable, Agent[Tick]] => Option[Tradable]) {

  def apply(information: Map[Tradable, Agent[Tick]]): Option[Tradable]

}
