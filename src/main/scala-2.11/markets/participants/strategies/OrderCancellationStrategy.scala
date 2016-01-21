package markets.participants.strategies

import markets.orders.Order

import scala.collection.mutable


trait OrderCancellationStrategy {

  def cancelOneOf[T <: mutable.Iterable[Order]](outstandingOrders: T): Option[Order]

}
