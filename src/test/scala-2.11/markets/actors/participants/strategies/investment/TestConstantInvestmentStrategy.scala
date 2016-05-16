package markets.actors.participants.strategies.investment

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


/** [[markets.actors.participants.strategies.investment.InvestmentStrategy `InvestmentStrategy`]]
  * that returns the same [[markets.tradables.Tradable `Tradable`]] every time.
  * @param tradable some [[markets.tradables.Tradable `Tradable`]].
  * @tparam T either [[markets.orders.AskOrder `AskOrder`]] or
  *           [[markets.orders.BidOrder `BidOrder`]], depending.
  */
class TestConstantInvestmentStrategy[T <: Order](tradable: Option[Tradable]) extends
  InvestmentStrategy[T] {

  def apply(information: Map[Tradable, Agent[Tick]]): Option[Tradable] = tradable

}
