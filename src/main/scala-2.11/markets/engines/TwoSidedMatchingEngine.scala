package markets.engines

import markets.engines.orderbooks.OrderBook
import markets.orders.{AskOrder, BidOrder, Order}

import scala.util.Try


trait TwoSidedMatchingEngine extends MatchingEngine {
  this: MatchingSchedule =>

  def askOrderBook: OrderBook[AskOrder, Iterable[AskOrder]]

  def bidOrderBook: OrderBook[BidOrder, Iterable[BidOrder]]

  /** Remove and return a specific order one of the order books.
    *
    * @return if the order can not be found in the order book, `None`; else `Some(residualOrder)`.
    * @note Removal of the order is a side effect.
    */
  def cancel(existing: Order): Option[Order] = existing match {
    case order: AskOrder => askOrderBook.pop(order)
    case order: BidOrder => bidOrderBook.pop(order)
  }

}
