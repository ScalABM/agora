package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.tradables.Tradable


class BidOrderBook[B <: BidOrder](tradable: Tradable) extends OrderBook[B](tradable)


object BidOrderBook {

  /** Create a `BidOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `BidOrder` instances contained in the `BidOrderBook` should be for the same `Tradable`.
    * @tparam B type of `BidOrder` stored in the order book.
    */
  def apply[B <: BidOrder](tradable: Tradable): BidOrderBook[B] = new BidOrderBook[B](tradable)

}