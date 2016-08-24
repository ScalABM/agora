package markets.engines.orderbooks

import markets.orders.{AskOrder, BidOrder, Order}
import markets.tradables.Tradable

import scala.util.Random


class ConcurrentOrderBookSpec extends AbstractOrderBookSpec {

  val prng = new Random(24)

  def askOrderBookFactory(tradable: Tradable) = ConcurrentOrderBook[AskOrder](tradable)

  def bidOrderBookFactory(tradable: Tradable) = ConcurrentOrderBook[BidOrder](tradable)

}
