package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder


class AskOrderBook[A <: AskOrder](tradable: Tradable) extends OrderBook[A](tradable)


object AskOrderBook {

  /** Create an `AskOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `AskOrder` instances contained in the `AskOrderBook` should be for the same `Tradable`.
    * @tparam A type of `Order` stored in the order book.
    */
  def apply[A <: AskOrder](tradable: Tradable): AskOrderBook[A] = new AskOrderBook[A](tradable)

}