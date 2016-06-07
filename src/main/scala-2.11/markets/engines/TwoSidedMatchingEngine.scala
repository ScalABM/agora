package markets.engines

import markets.engines.orderbooks.OrderBook
import markets.orders.{AskOrder, BidOrder}


abstract class TwoSidedMatchingEngine[A <: OrderBook[AskOrder, Iterable[AskOrder]], B <: OrderBook[BidOrder, Iterable[BidOrder]]]
  extends MatchingEngine {

  def askOrderBook:
}
