package markets.orderbooks.mutable

import markets.orderbooks.OrderBook
import markets.orders.Order

import scala.collection.mutable


/** Base trait for all order books.
  *
  * An order book is a collection of orders (typically either ask or bid orders).
  *
  * @tparam A the type of orders stored in the order book.
  * @tparam B the type of underlying collection used to store the orders.
  */
trait MutableOrderBook[A <: Order, B <: mutable.Iterable[A]] extends OrderBook[A, B]{

  protected val backingStore: B

}

