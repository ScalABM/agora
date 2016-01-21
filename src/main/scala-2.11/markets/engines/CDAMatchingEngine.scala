/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.engines

import markets.orders.limit.LimitOrderLike
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.orders.orderings.PriceOrdering
import markets.orders.{AskOrder, BidOrder, Order}

import scala.annotation.tailrec
import scala.collection.{immutable, mutable}


/** Continuous Double Auction (CDA) Matching Engine. */
class CDAMatchingEngine(askOrdering: PriceOrdering[AskOrder],
                        bidOrdering: PriceOrdering[BidOrder],
                        initialPrice: Long) extends MutableMatchingEngine {

  /** A sorted collection of ask orders.
    *
    * @note This is an immutable view into a mutable private collection.
    */
  def askOrderBook: immutable.TreeSet[AskOrder] = {
    immutable.TreeSet.empty[AskOrder](askOrdering) ++ _askOrderBook
  }

  /** A sorted collection of bid orders.
    *
    * @note This is an immutable view into a mutable private collection.
    */
  def bidOrderBook: immutable.TreeSet[BidOrder] = {
    immutable.TreeSet.empty[BidOrder](bidOrdering) ++ _bidOrderBook
  }

  /** Find a match for an incoming order.
    *
    * @param incoming the order to be matched.
    * @return a collection of matches.
    */
  def findMatch(incoming: Order): Option[immutable.Queue[Matching]] = {

    incoming match {
      case order: AskOrder =>
        val matches = accumulateBidOrders(order, immutable.Queue.empty[Matching])
        if (matches.isEmpty) None else Some(matches)
      case order: BidOrder =>
        val matches = accumulateAskOrders(order, immutable.Queue.empty[Matching])
        if (matches.isEmpty) None else Some(matches)
      case _ => None //todo consider making Order sealed with AskOrder and BidOrder as subclasses
    }
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
      case (_, _: LimitOrderLike) =>  // Existing limit order always determines price
        mostRecentPrice = existing.price  // SIDE EFFECT!
        mostRecentPrice
      case (_, _: MarketAskOrder) =>
        bestLimitAskOrder match {
          case Some(limitOrder) =>
            val possiblePrices = immutable.Seq(incoming.price, limitOrder.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.min  // SIDE EFFECT!
            mostRecentPrice
          case None =>
            mostRecentPrice = incoming.price  // SIDE EFFECT!
            mostRecentPrice
        }
      case (_, _: MarketBidOrder) =>
        bestLimitBidOrder match {
          case Some(limitOrder) =>
            val possiblePrices = immutable.Seq(incoming.price, limitOrder.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.max  // SIDE EFFECT!
            mostRecentPrice
          case None =>
            mostRecentPrice = incoming.price  // SIDE EFFECT!
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

  /** Remove an order from the matching engine. */
  def remove(existing: Order): Option[Order] = {
    existing match {
      case _: AskOrder =>
        _askOrderBook.find(o => o.uuid == existing.uuid) match {
          case result @ Some(residualOrder) =>
            _askOrderBook.remove(residualOrder) // SIDE EFFECT!
            result
          case _ => None
        }
      case _: BidOrder =>
        _bidOrderBook.find(o => o.uuid == existing.uuid) match {
          case result @ Some(residualOrder) =>
            _bidOrderBook.remove(residualOrder) // SIDE EFFECT!
            result
          case _ => None
        }
      case _ => None //todo consider making Order sealed with AskOrder and BidOrder as subclasses
    }
  }

  /* Mutable collection of ask orders for internal use only! */
  protected val _askOrderBook = mutable.TreeSet.empty[AskOrder](askOrdering)

  /* Mutable collection of bid orders for internal use only! */
  protected val _bidOrderBook = mutable.TreeSet.empty[BidOrder](bidOrdering)

  /* Cached value of most recent transaction price for internal use only. */
  private[this] var mostRecentPrice = initialPrice

  @tailrec
  private[this] def accumulateAskOrders(incoming: BidOrder,
                                        matchings: immutable.Queue[Matching]): immutable.Queue[Matching] = {
    _askOrderBook.headOption match {
      case Some(askOrder) if incoming.crosses(askOrder) =>

        _askOrderBook -= askOrder  // SIDE EFFECT!
        val residualQuantity = incoming.quantity - askOrder.quantity
        val price = formPrice(incoming, askOrder)
        val quantity = formQuantity(incoming, askOrder)

        if (residualQuantity < 0) {  // incoming order is smaller than existing order
          val (_, residualAskOrder) = askOrder.split(-residualQuantity)
          val matching = Matching(askOrder, incoming, price, quantity, Some(residualAskOrder), None)
          _askOrderBook.add(residualAskOrder)  // SIDE EFFECT!
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
        _bidOrderBook.add(incoming)  // SIDE EFFECT!
        matchings
    }
  }

  @tailrec
  private[this] def accumulateBidOrders(incoming: AskOrder,
                                        matchings: immutable.Queue[Matching]): immutable.Queue[Matching] = {
    _bidOrderBook.headOption match {
      case Some(bidOrder) if incoming.crosses(bidOrder) =>

        _bidOrderBook.remove(bidOrder)  // SIDE EFFECT!
        val residualQuantity = incoming.quantity - bidOrder.quantity
        val price = formPrice(incoming, bidOrder)
        val quantity = formQuantity(incoming, bidOrder)

        if (residualQuantity < 0) { // incoming order is smaller than existing order!
          val (_, residualBidOrder) = bidOrder.split(-residualQuantity)
          val matching = Matching(incoming, bidOrder, price, quantity, None, Some(residualBidOrder))
          _bidOrderBook.add(residualBidOrder)  // SIDE EFFECT!
          matchings.enqueue(matching)
        } else if (residualQuantity == 0) {  // no rationing for incoming order!
          val matching = Matching(incoming, bidOrder, price, quantity, None, None)
          matchings.enqueue(matching)
        } else {  // incoming order is larger than existing order and will be rationed!
          val (_, residualAskOrder) = incoming.split(residualQuantity)
          val matching = Matching(incoming, bidOrder, price, quantity, Some(residualAskOrder), None)
          accumulateBidOrders(residualAskOrder, matchings.enqueue(matching))
        }
      case _ => // existingOrders is empty or incoming order does not cross best existing order.
        _askOrderBook.add(incoming)  // SIDE EFFECT!
        matchings
    }
  }

}


object CDAMatchingEngine {

  def apply(askOrdering: PriceOrdering[AskOrder],
            bidOrdering: PriceOrdering[BidOrder],
            initialPrice: Long): CDAMatchingEngine = {
    new CDAMatchingEngine(askOrdering, bidOrdering, initialPrice)
  }

}