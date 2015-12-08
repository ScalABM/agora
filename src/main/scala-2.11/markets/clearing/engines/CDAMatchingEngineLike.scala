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

import markets.clearing.strategies.PriceFormationStrategy
import markets.clearing.engines.matches.{Match, PartialMatch, TotalMatch}
import markets.orders.{AskOrder, BidOrder, Order}
import markets.orders.orderings.PriceOrdering

import scala.annotation.tailrec
import scala.collection.{immutable, mutable}


trait CDAMatchingEngineLike extends MutableMatchingEngine {
  this: PriceFormationStrategy =>

  protected val askOrdering: PriceOrdering[AskOrder]

  protected val bidOrdering: PriceOrdering[BidOrder]

  protected val _askOrderBook: mutable.TreeSet[AskOrder]

  protected val _bidOrderBook: mutable.TreeSet[BidOrder]

  /** A sorted collection of ask orders.
    * @note This is an immutable view into a mutable private collection.
    */
  def askOrderBook: immutable.TreeSet[AskOrder] = {
    _askOrderBook.to[immutable.TreeSet[AskOrder]]
  }

  /** A sorted collection of bid orders.
    * @note This is an immutable view into a mutable private collection.
    */
  def bidOrderBook: immutable.TreeSet[BidOrder] = {
    _bidOrderBook.to[immutable.TreeSet[BidOrder]]
  }

  protected def crosses(incomingOrder: Order, existingOrder: Order): Boolean = {
    (incomingOrder, existingOrder) match {
      case (ask: AskOrder, bid: BidOrder) => ask.price <= bid.price
      case (bid: BidOrder, ask: AskOrder) => bid.price >= ask.price
    }
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
          _askOrderBook += residualOrder  // SIDE EFFECT!
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
        _bidOrderBook += incoming  // SIDE EFFECT!
        matches
    }
  }

  @tailrec
  private def findMatchingBidOrders(incoming: AskOrder,
                                    matches: immutable.Queue[Match]): immutable.Queue[Match] = {
    _bidOrderBook.headOption match {
      case Some(bidOrder) if crosses(incoming, bidOrder) =>

        _bidOrderBook -= bidOrder  // SIDE EFFECT!
        val residualQuantity = incoming.quantity - bidOrder.quantity
        val price = formPrice(incoming, bidOrder)

        if (residualQuantity < 0) {
          val totalMatch = TotalMatch(bidOrder, incoming, price)
          // add residualOrder back into orderBook!
          val residualOrder = bidOrder.split(-residualQuantity)
          _bidOrderBook += residualOrder  // SIDE EFFECT!
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
        _askOrderBook += incoming  // SIDE EFFECT!
        matches
    }
  }

  /** Remove an order from the matching engine. */
  def remove(order: Order): Option[Order] = {
    case order: AskOrder =>
      _askOrderBook.find(o => o.uuid == order.uuid) match {
        case result @ Some(residualOrder) =>
          _askOrderBook -= residualOrder // SIDE EFFECT!
          result
        case _ => None
      }
    case order: BidOrder =>
      _bidOrderBook.find(o => o.uuid == order.uuid) match {
        case result @ Some(residualOrder) =>
          _bidOrderBook -= residualOrder  // SIDE EFFECT!
          result
        case _ => None
      }
  }

}
