package markets.actors.participants.strategies.trading

import markets.orders.Order


trait ConstantPrice[T <: Order] {
  this: TradingStrategy[T] =>

  def price: Option[Long]

}
