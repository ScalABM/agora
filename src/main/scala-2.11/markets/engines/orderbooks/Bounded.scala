package markets.engines.orderbooks

import markets.orders.Order


trait Bounded[A <: Order] {
  this: OrderBook[A] =>

  def depth: Int

}
