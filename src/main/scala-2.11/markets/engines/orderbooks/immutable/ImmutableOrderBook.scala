package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.OrderBook
import markets.orders.Order

import scala.collection.immutable


/** Abstract class defining an order book whose underlying backing store is an immutable collection.
  *
  * @tparam A type of `Order` stored in the order book.
  * @tparam CC type of immutable, iterable collection used as the backing store.
  * @note the covariant type annotation on `CC` implies that if some immutable, iterable collection
  *       `CC2` is a sub-type of another immutable, iterable collection `CC1`, then
  *       `ImmutableOrderBook[A,CC2]` is a sub-type of `ImmutableOrderBook[A,CC1]`.
  */
abstract class ImmutableOrderBook[A <: Order, +CC <: immutable.Iterable[A]] extends OrderBook[A, CC]


