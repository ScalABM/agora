package markets.orderbooks.mutable

import java.util.UUID

import markets.tradables.Tradable
import markets.tradables.orders.Order

import scala.collection.mutable


class HashOrderBook[O <: Order](val tradable: Tradable) extends OrderBook[O, mutable.HashMap[UUID, O]] {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders = mutable.HashMap.empty[UUID, O]

}
