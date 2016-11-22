package org.economicsl.agora.markets.auctions.mutable.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent

import scala.collection.mutable


trait BidOrderBook[B <: BidOrder with Persistent] extends OrderBook[B]


object BidOrderBook {

  /** Create a `BidOrderBook` instance for a particular `Tradable`.
    *
    * @param tradable all `BidOrder` instances contained in the `BidOrderBook` should be for the same `Tradable`.
    * @tparam B type of `BidOrder` stored in the order book.
    */
  def apply[B <: BidOrder with Persistent](tradable: Tradable): BidOrderBook[B] = DefaultImpl[B](tradable)

  private[this] case class DefaultImpl[B <: BidOrder with Persistent](tradable: Tradable) extends BidOrderBook[B] {

    /* underlying collection used to store `BidOrder` instances. */
    protected[orderbooks] val existingOrders = mutable.HashMap.empty[UUID, B]

  }

}