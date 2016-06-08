package markets.engines

import markets.engines.orderbooks.OrderBook
import markets.orders.Order


trait OneSidedMatchingEngine[A <: Order] extends MatchingEngine {
  this: MatchingSchedule =>

  def orderBook: OrderBook[A, Iterable[A]]

  /** Remove and return a specific order from of the order books.
    *
    * @return if the order can not be found in the order book, `None`; else `Some(residualOrder)`.
    * @note Removal of the order is a side effect.
    */
  def cancel(existing: A): Option[A] = {
    require(existing.tradable == tradable)
    orderBook.pop(existing)
  }

}
