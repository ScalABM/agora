package markets.actors.participants.strategies.investment

import markets.orders.Order
import markets.tradables.Tradable


trait ConstantTradable[T <: Order] {
  this: InvestmentStrategy[T] =>

  def tradable: Tradable

}
