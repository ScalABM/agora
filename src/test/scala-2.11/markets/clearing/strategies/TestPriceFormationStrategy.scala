package markets.clearing.strategies

import markets.clearing.engines.MatchingEngine
import markets.orders.Order


trait TestPriceFormationStrategy extends PriceFormationStrategy {
  this: MatchingEngine =>

  def formPrice(incomingOrder: Order, existingOrder: Order): Long = 1

}
