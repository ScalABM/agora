package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.OrderBookSpec
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable

import scala.util.Random


class ImmutableTreeSetOrderBookSpec extends OrderBookSpec("ImmutableTreeSetOrderBook") {

  val invalidTradable = Tradable("APPL")

  val validTradable = Tradable("GOOG")

  val prng = new Random(11)

  def askOrderBook() = ImmutableTreeSetOrderBook[AskOrder](AskPriceTimeOrdering, validTradable)

  def bidOrderBook() = ImmutableTreeSetOrderBook[BidOrder](BidPriceTimeOrdering, validTradable)

}
