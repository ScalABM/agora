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
package markets.clearing.engines

import markets.clearing.engines.matches.{Match, PartialMatch, TotalMatch}
import markets.orders.limit.{LimitBidOrder, LimitAskOrder, LimitOrderLike}
import markets.orders.market.{MarketOrderLike, MarketAskOrder, MarketBidOrder}
import markets.orders.{AskOrder, BidOrder, Order}
import markets.orders.orderings.PriceOrdering

import scala.annotation.tailrec
import scala.collection.{immutable, mutable}


/** Continuous Double Auction (CDA) Matching Engine. */
class CDAMatchingEngine(askOrdering: PriceOrdering[AskOrder],
                        bidOrdering: PriceOrdering[BidOrder],
                        initialPrice: Long) extends MutableMatchingEngine {

  /* Mutable collection of ask orders for internal use only! */
  protected val _askOrderBook = mutable.TreeSet.empty[AskOrder](askOrdering)

  /* Mutable collection of bid orders for internal use only! */
  protected val _bidOrderBook = mutable.TreeSet.empty[BidOrder](bidOrdering)

  /* Cached value of most recent transaction price for internal use only. */
  private var _mostRecentPrice = initialPrice

  protected def crosses(incomingOrder: Order, existingOrder: Order): Boolean = {
    (incomingOrder, existingOrder) match {
      case (ask: AskOrder, bid: BidOrder) => ask.price <= bid.price
      case (bid: BidOrder, ask: AskOrder) => bid.price >= ask.price
    }
  }

  /** A sorted collection of ask orders.
    * @note This is an immutable view into a mutable private collection.
    */
  def askOrderBook: immutable.TreeSet[AskOrder] = {
    immutable.TreeSet.empty[AskOrder](askOrdering) ++ _askOrderBook
  }

  /** A sorted collection of bid orders.
    * @note This is an immutable view into a mutable private collection.
    */
  def bidOrderBook: immutable.TreeSet[BidOrder] = {
    immutable.TreeSet.empty[BidOrder](bidOrdering) ++ _bidOrderBook
  }

  def findMatch(incomingOrder: Order): Option[immutable.Iterable[Match]] = {

    incomingOrder match {
      case order: AskOrder =>
        val matches = findMatchingBidOrders(order, immutable.Queue.empty[Match])
        if (matches.isEmpty) None else Some(matches)
      case order: BidOrder =>
        val matches = findMatchingAskOrders(order, immutable.Queue.empty[Match])
        if (matches.isEmpty) None else Some(matches)
    }

  }

  @tailrec
  private def findMatchingAskOrders(incoming: BidOrder,
                                    matches: immutable.Queue[Match]): immutable.Queue[Match] = {
    _askOrderBook.headOption match {
      case Some(askOrder) if crosses(incoming, askOrder) =>

        _askOrderBook -= askOrder  // SIDE EFFECT!
        val residualQuantity = incoming.quantity - askOrder.quantity
        val price = formPrice(incoming, askOrder)

        if (residualQuantity < 0) {
          val totalMatch = TotalMatch(askOrder, incoming, price)
          // add residualOrder back into orderBook!
          val residualOrder = askOrder.split(-residualQuantity)
          _askOrderBook.add(residualOrder)  // SIDE EFFECT!
          matches.enqueue(totalMatch)
        } else if (residualQuantity == 0) {  // no rationing for incoming order!
          val totalMatch = TotalMatch(askOrder, incoming, price)
          matches.enqueue(totalMatch)
        } else {  // incoming order is larger than existing order and will be rationed!
          val partialMatch = PartialMatch(askOrder, incoming, price)
          val residualOrder = incoming.split(residualQuantity)
          findMatchingAskOrders(residualOrder, matches.enqueue(partialMatch))
        }

      case _ => // existingOrders is empty or incoming order does not cross best existing order.
        _bidOrderBook.add(incoming)  // SIDE EFFECT!
        matches
    }
  }

  @tailrec
  private def findMatchingBidOrders(incoming: AskOrder,
                                    matches: immutable.Queue[Match]): immutable.Queue[Match] = {
    _bidOrderBook.headOption match {
      case Some(bidOrder) if crosses(incoming, bidOrder) =>

        _bidOrderBook.remove(bidOrder)  // SIDE EFFECT!
        val residualQuantity = incoming.quantity - bidOrder.quantity
        val price = formPrice(incoming, bidOrder)

        if (residualQuantity < 0) {
          val totalMatch = TotalMatch(bidOrder, incoming, price)
          // add residualOrder back into orderBook!
          val residualOrder = bidOrder.split(-residualQuantity)
          _bidOrderBook.add(residualOrder)  // SIDE EFFECT!
          matches.enqueue(totalMatch)
        } else if (residualQuantity == 0) {  // no rationing for incoming order!
          val totalMatch = TotalMatch(bidOrder, incoming, price)
          matches.enqueue(totalMatch)
        } else {  // incoming order is larger than existing order and will be rationed!
          val partialMatch = PartialMatch(bidOrder, incoming, price)
          val residualOrder = incoming.split(residualQuantity)
          findMatchingBidOrders(residualOrder, matches.enqueue(partialMatch))
        }
      case _ => // existingOrders is empty or incoming order does not cross best existing order.
        _askOrderBook.add(incoming)  // SIDE EFFECT!
        matches
    }
  }

  /** Implements price formation rules for limit and market orders.
    *
    * This matching engine uses the “Best limit” price improvement rule: if the opposite book
    * does have limit orders, then the trade settles at the better of two prices (either the
    * incoming order’s limit or the best limit from the opposite book) the term “better of two
    * prices” is from the point of view of the incoming limit order. In other words, if incoming
    * limit order would have crossed with outstanding opposite “best limit” order in the absence
    * of market order, then the trade would execute at that, potentially improved, “best limit”
    * price.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite book.
    * @return the price at which the trade between the two orders will execute.
    */
  def formPrice(incoming: Order, existing: Order): Long = {
    (incoming, existing) match {

      // With limit orders the price is always determined by the existing order!
      case (_: LimitOrderLike, _: LimitOrderLike) =>
        _mostRecentPrice = existing.price  // SIDE EFFECT!
        _mostRecentPrice
      case (_: LimitAskOrder, _: MarketBidOrder) =>
        bestLimitBidOrder match {
          case Some(limitOrder) =>
            val possiblePrices = immutable.Seq(incoming.price, limitOrder.price, _mostRecentPrice)
            _mostRecentPrice = possiblePrices.max
            _mostRecentPrice
          case None =>
            _mostRecentPrice = incoming.price
            _mostRecentPrice
        }
      case (_: LimitBidOrder, _: MarketAskOrder) =>
        bestLimitAskOrder match {
          case Some(limitOrder) =>
            val possiblePrices = immutable.Seq(incoming.price, limitOrder.price, _mostRecentPrice)
            _mostRecentPrice = possiblePrices.min
            _mostRecentPrice
          case None =>
            _mostRecentPrice = incoming.price
            _mostRecentPrice
        }

      // Handle incoming market orders
      case (_: MarketOrderLike, _: LimitOrderLike) =>
        _mostRecentPrice = existing.price
        _mostRecentPrice
      case (_: MarketAskOrder, _: MarketBidOrder) =>
        bestLimitBidOrder match {
          case Some(limitOrder) =>
            _mostRecentPrice = math.max(limitOrder.price, _mostRecentPrice)
            _mostRecentPrice
          case None => _mostRecentPrice
        }
      case (_: MarketBidOrder, _: MarketAskOrder) =>
        bestLimitAskOrder match {
          case Some(limitOrder) =>
            _mostRecentPrice = math.min(limitOrder.price, _mostRecentPrice)
            _mostRecentPrice
          case None => _mostRecentPrice
        }

    }
  }

  /** Remove an order from the matching engine. */
  def remove(order: Order): Option[Order] = {
    order match {
      case _: AskOrder =>
        _askOrderBook.find(o => o.uuid == order.uuid) match {
          case result@Some(residualOrder) =>
            _askOrderBook.remove(residualOrder) // SIDE EFFECT!
            result
          case _ => None
        }
      case _: BidOrder =>
        _bidOrderBook.find(o => o.uuid == order.uuid) match {
          case result@Some(residualOrder) =>
            _bidOrderBook.remove(residualOrder) // SIDE EFFECT!
            result
          case _ => None
        }
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