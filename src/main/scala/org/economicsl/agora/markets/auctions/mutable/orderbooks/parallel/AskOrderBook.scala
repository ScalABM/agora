package org.economicsl.agora.markets.auctions.mutable.orderbooks.parallel

import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent


class AskOrderBook[A <: AskOrder with Persistent](tradable: Tradable) extends OrderBook[A](tradable)


object AskOrderBook {

  /** Create an `AskOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `Orders` contained in the `AskOrderBook` should be for the same `Tradable`.
    * @tparam A type of `Order` stored in the order book.
    */
  def apply[A <: AskOrder with Persistent](tradable: Tradable): AskOrderBook[A] = new AskOrderBook[A](tradable)

}