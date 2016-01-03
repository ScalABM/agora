package markets.participants.strategies

import markets.orders.Order

import scala.collection.mutable


trait OrderCancellationStrategy {

  def cancelOneOf(outstandingOrders: mutable.Iterable[Order]): Option[Order]

}
