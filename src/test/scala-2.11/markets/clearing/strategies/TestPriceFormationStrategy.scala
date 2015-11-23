package markets.clearing.strategies

import markets.clearing.engines.MatchingEngineLike
import markets.orders.Order


trait TestPriceFormationStrategy extends PriceFormationStrategy {
  this: MatchingEngineLike =>

  def formPrice(incomingOrder: Order, existingOrder: Order): Long = 1

}
