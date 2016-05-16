package markets.actors.participants.strategies.investment

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


trait TradableValuations[T <: Order] {
  this: InvestmentStrategy[T] =>

  def valuations: Map[Tradable, Long]

  /** Collection of tickers for over-valued tradables.
    *
    * @param information the collection of relevant information for making investment decision.
    * @return the subset of information relevant for those tradables that are over-valued given
    *         current `valuations`.
    */
  protected final def overValuedTradables(information: Map[Tradable, Agent[Tick]]) = {
    information.filter {
      case (tradable, ticker) => valuations(tradable) <= ticker.get.price
    }
  }

  /** Collection of tickers for under-valued tradables.
    *
    * @param information the collection of relevant information for making investment decision.
    * @return the subset of information relevant for those tradables that are under-valued given
    *         current `valuations`.
    */
  protected final def underValuedTradables(information: Map[Tradable, Agent[Tick]]) = {
    information.filter {
      case (tradable, ticker) => valuations(tradable) >= ticker.get.price
    }
  }

}
