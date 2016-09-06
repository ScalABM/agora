/*
Copyright 2016 ScalABM

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
package markets

import markets.engines.{Matching, PriorityMatchingEngine}
import markets.orders.limit.LimitOrder
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.orders.{AskOrder, BidOrder, Order}
import markets.tradables.Tradable

import scala.annotation.tailrec
import scala.collection.immutable.Queue


/** Continuous Double Auction (CDA) Matching Engine. */
class SecuritiesMarket(initialPrice: Long, tradable: Tradable)
                      (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder]) {

  /** Fill an incoming `Order`.
    *
    * @param incoming the order to be matched.
    * @return a collection of matches.
    */
  def fill(incoming: Order): Option[Queue[Matching]] = incoming match {
    case order: AskOrder =>
      val matches = accumulateBidOrders(order, Queue.empty[Matching])
      if (matches.isEmpty) None else Some(matches)
    case order: BidOrder =>
      val matches = accumulateAskOrders(order, Queue.empty[Matching])
      if (matches.isEmpty) None else Some(matches)
    case _ => None // todo consider making Order sealed with AskOrder and BidOrder as subclasses
  }

  /** Implements price formation rules for limit and market orders.
    *
    * This matching engine uses a “Best limit” price improvement rule: if the opposite book
    * does have limit orders, then the trade settles at the better of three prices (either the
    * incoming order’s limit, the best limit from the opposite book, or the most recent trade
    * price). The term “better of three prices” is from the point of view of the incoming limit
    * order.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite book.
    * @return the price at which a trade between the two orders will execute.
    */
  def formPrice(incoming: Order, existing: Order): Long = {
    (incoming, existing) match {
      case (_, _: LimitOrder) =>  // Existing limit order always determines price
        mostRecentPrice = existing.price  // SIDE EFFECT!
        mostRecentPrice
      case (_, _: MarketAskOrder) =>
        matchingEngine.askOrderBook.find(order => order.isInstanceOf[LimitOrder]) match {
          case Some(limitOrder) =>
            val possiblePrices = Seq(incoming.price, limitOrder.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.min  // SIDE EFFECT!
            mostRecentPrice
          case None =>
            val possiblePrices = Seq(incoming.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.min  // SIDE EFFECT!
            mostRecentPrice
        }
      case (_, _: MarketBidOrder) =>
        matchingEngine.bidOrderBook.find(order => order.isInstanceOf[LimitOrder]) match {
          case Some(limitOrder) =>
            val possiblePrices = Seq(incoming.price, limitOrder.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.max  // SIDE EFFECT!
            mostRecentPrice
          case None =>
            val possiblePrices = Seq(incoming.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.max  // SIDE EFFECT!
            mostRecentPrice
        }
    }
  }

  /** Rule for choosing the quantity for a Fill.
    *
    * @param incoming
    * @param existingOrder
    * @return
    */
  def formQuantity(incoming: Order, existingOrder: Order): Long = {
    math.min(incoming.quantity, existingOrder.quantity)
  }


  @tailrec
  private[this] def accumulateAskOrders(incoming: BidOrder,
                                        matchings: Queue[Matching]): Queue[Matching] = {
    matchingEngine.matchWithAskOrder(incoming) match {
      case Some(askOrder) =>
        val residualQuantity = incoming.quantity - askOrder.quantity
        val price = formPrice(incoming, askOrder)
        val quantity = formQuantity(incoming, askOrder)
        if (residualQuantity < 0) {  // incoming order is smaller than existing order
          val (_, residualAskOrder) = askOrder.split(-residualQuantity)
          val matching = Matching(askOrder, incoming, price, quantity, Some(residualAskOrder), None)
          matchingEngine.askOrderBook.add(residualAskOrder)  // SIDE EFFECT!
          matchings.enqueue(matching)
        } else if (residualQuantity == 0) {  // no rationing for incoming order!
          val matching = Matching(askOrder, incoming, price, quantity, None, None)
          matchings.enqueue(matching)
        } else {  // incoming order is larger than existing order and will be rationed!
          val (_, residualBidOrder) = incoming.split(residualQuantity)
          val matching = Matching(askOrder, incoming, price, quantity, None, Some(residualBidOrder))
          accumulateAskOrders(residualBidOrder, matchings.enqueue(matching))
        }

      case _ => // existingOrders is empty or incoming order does not cross best existing order.
        matchings
    }
  }

  @tailrec
  private[this] def accumulateBidOrders(incoming: AskOrder,
                                        matchings: Queue[Matching]): Queue[Matching] = {
    matchingEngine.matchWithBidOrder(incoming) match {
      case Some(bidOrder) =>
        val residualQuantity = incoming.quantity - bidOrder.quantity
        val price = formPrice(incoming, bidOrder)
        val quantity = formQuantity(incoming, bidOrder)
        if (residualQuantity < 0) { // incoming order is smaller than existing order!
          val (_, residualBidOrder) = bidOrder.split(-residualQuantity)
          val matching = Matching(incoming, bidOrder, price, quantity, None, Some(residualBidOrder))
          matchingEngine.bidOrderBook.add(residualBidOrder)  // SIDE EFFECT!
          matchings.enqueue(matching)
        } else if (residualQuantity == 0) {  // no rationing for incoming order!
          val matching = Matching(incoming, bidOrder, price, quantity, None, None)
          matchings.enqueue(matching)
        } else {  // incoming order is larger than existing order and will be rationed!
          val (_, residualAskOrder) = incoming.split(residualQuantity)
          val matching = Matching(incoming, bidOrder, price, quantity, Some(residualAskOrder), None)
          accumulateBidOrders(residualAskOrder, matchings.enqueue(matching))
        }
      case None => // existingOrders is empty or incoming order does not cross best existing order.
        matchings
    }
  }

  protected[markets] val matchingEngine = PriorityMatchingEngine(tradable)(askOrdering, bidOrdering)

  /* Cached value of most recent transaction price for internal use only. */
  private[this] var mostRecentPrice = initialPrice

}

object SecuritiesMarket {

  def apply(initialPrice: Long, tradable: Tradable)
           (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder]): SecuritiesMarket = {
    new SecuritiesMarket(initialPrice, tradable)(askOrdering, bidOrdering)
  }
}