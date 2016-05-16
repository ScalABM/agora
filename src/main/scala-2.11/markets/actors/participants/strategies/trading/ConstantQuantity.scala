package markets.actors.participants.strategies.trading

import markets.orders.Order


trait ConstantQuantity[T <: Order] {
  this: TradingStrategy[T] =>

  def quantity: Long

}
