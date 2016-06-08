package markets.engines.orderbooks.mutable

import markets.engines.orderbooks.OrderBookSpec
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable

import scala.util.Random


class MutableTreeSetOrderBookSpec extends OrderBookSpec("MutableTreeSetOrderBook") {

  val invalidTradable = Tradable("APPL")

  val validTradable = Tradable("GOOG")

  val prng = new Random(14)

  def askOrderBook() = MutableTreeSetOrderBook[AskOrder](AskPriceTimeOrdering, validTradable)

  def bidOrderBook() = MutableTreeSetOrderBook[BidOrder](BidPriceTimeOrdering, validTradable)

}
