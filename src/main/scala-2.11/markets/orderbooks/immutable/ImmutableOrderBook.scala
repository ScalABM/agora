package markets.orderbooks.immutable

import markets.orders.Order

import scala.collection.immutable


/** Base trait for all immutable order books.
  *
  * An order book is a collection of orders (typically either ask or bid orders).
  *
  * @tparam A the type of orders stored in the order book.
  * @tparam B the type of underlying collection used to store the orders.
  */
trait ImmutableOrderBook[A <: Order, B <: immutable.Iterable[A]] {

  protected var backingStore: B

}

