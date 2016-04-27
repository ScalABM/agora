package markets.orderbooks.immutable

import markets.orderbooks.OrderBooks
import markets.orders.{AskOrder, BidOrder}

import scala.collection.immutable


/** Mixin trait providing immutable order books.
  *
  * @tparam A the type of orders stored in the order book.
  * @tparam B the type of underlying immutable collection used to store the orders.
  */
trait ImmutableOrderBooks[A <: immutable.Iterable[AskOrder], B <: immutable.Iterable[BidOrder]]
  extends OrderBooks[A, B]

