package markets.orderbooks.mutable

import java.util.UUID

import markets.tradables.Tradable
import markets.tradables.orders.Order

import scala.collection.mutable


class LinkedHashOrderBook[O <: Order](val tradable: Tradable) extends OrderBook[O, mutable.LinkedHashMap[UUID, O]] {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders = mutable.LinkedHashMap.empty[UUID, O]

}


object LinkedHashOrderBook {

  def apply[O <: Order](tradable: Tradable): LinkedHashOrderBook[O] = {
    new LinkedHashOrderBook[O](tradable)
  }

  def apply[O <: Order](initialOrders: Iterable[O], tradable: Tradable): LinkedHashOrderBook[O] = {
    val orderBook = LinkedHashOrderBook[O](tradable)
    initialOrders.foreach(order => orderBook.add(order))
    orderBook
  }

}
