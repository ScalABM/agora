package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.Tradable

import scala.collection.mutable


class SortedAskOrderBook[A <: AskOrder](tradable: Tradable)(implicit ordering: Ordering[A])
  extends AskOrderBook[A](tradable) with SortedOrders[A] {

  protected[orderbooks] val sortedOrders = mutable.TreeSet.empty[A](ordering)

}


object SortedAskOrderBook {

  /** Create an `AskOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `AskOrder` instances contained in the `AskOrderBook` should be for the same `Tradable`.
    * @tparam A type of `AskOrder` stored in the order book.
    */
  def apply[A <: AskOrder](tradable: Tradable)(implicit ordering: Ordering[A]): SortedAskOrderBook[A] = {
    new SortedAskOrderBook[A](tradable)(ordering)
  }

}