package markets.engines.orderbooks.mutable

import markets.engines.orderbooks.OrderBookSpec
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable

import scala.util.Random


class MutablePriorityQueueOrderBookSpec extends OrderBookSpec("MutablePriorityQueueOrderBook") {

  val invalidTradable = Tradable("APPL")

  val validTradable = Tradable("GOOG")

  val prng = new Random(42)

  def askOrderBook() = MutablePriorityQueueOrderBook[AskOrder](AskPriceTimeOrdering, validTradable)

  def bidOrderBook() = MutablePriorityQueueOrderBook[BidOrder](BidPriceTimeOrdering, validTradable)

}
