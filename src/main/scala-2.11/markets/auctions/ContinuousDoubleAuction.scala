/*
Copyright 2016 David R. Pugh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package markets.auctions

import markets.auctions.orderbooks.PriorityOrderBook
import markets.orders.limit.LimitOrder
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.orders.{AskOrder, BidOrder, Order}
import markets.tradables.Tradable

import scala.collection.immutable.Queue


/** Continuous Double Auction (CDA) Matching Engine. */
class ContinuousDoubleAuction(initialPrice: Long, tradable: Tradable)
                             (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder])
  extends TwoSidedAuction {

  val askOrderBook = PriorityOrderBook[AskOrder](tradable)(askOrdering)

  val bidOrderBook = PriorityOrderBook[BidOrder](tradable)(bidOrdering)

  /** Rule specifying the transaction price between two orders.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite book.
    * @return the price at which a trade between the two orders will execute.
    * @note The Continuous Double Auction (CDA) mechanism uses a “Best limit” price improvement
    *       rule: if the opposite order book does have limit orders, then the trade settles at
    *       the better of three prices (either the incoming order’s limit, the best limit from
    *       the opposite book, or the most recent trade price). The term “better of three prices”
    *       is from the point of view of the incoming order.
    */
  def formPrice(incoming: Order, existing: Order): Long = {
    (incoming, existing) match {
      case (_, _: LimitOrder) =>  // Existing limit order always determines price
        currentPrice = existing.price  // SIDE EFFECT!
        currentPrice
      case (_, _: MarketAskOrder) =>
        askOrderBook.priorityLimitOrder match {
          case Some(limitOrder) =>
            val possiblePrices = Seq(incoming.price, limitOrder.price, currentPrice)
            currentPrice = possiblePrices.min  // SIDE EFFECT!
            currentPrice
          case None =>
            val possiblePrices = Seq(incoming.price, currentPrice)
            currentPrice = possiblePrices.min  // SIDE EFFECT!
            currentPrice
        }
      case (_, _: MarketBidOrder) =>
        bidOrderBook.priorityLimitOrder match {
          case Some(limitOrder) =>
            val possiblePrices = Seq(incoming.price, limitOrder.price, currentPrice)
            currentPrice = possiblePrices.max  // SIDE EFFECT!
            currentPrice
          case None =>
            val possiblePrices = Seq(incoming.price, currentPrice)
            currentPrice = possiblePrices.max  // SIDE EFFECT!
            currentPrice
        }
    }
  }

  /* Cached value of most recent transaction price for internal use only. */
  private[this] var currentPrice = initialPrice

}

object ContinuousDoubleAuction {

  def apply(initialPrice: Long, tradable: Tradable)
           (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder]): ContinuousDoubleAuction = {
    new ContinuousDoubleAuction(initialPrice, tradable)(askOrdering, bidOrdering)
  }
}