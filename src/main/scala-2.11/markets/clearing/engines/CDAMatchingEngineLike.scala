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
import markets.orders.filled.{FilledOrder, PartialFilledOrder, TotalFilledOrder}
import markets.orders.orderings.PriceOrdering
import markets.orders.{AskOrder, BidOrder, Order}

import scala.annotation.tailrec
import scala.collection.immutable


trait CDAMatchingEngineLike extends MatchingEngineLike {
  this: PriceFormationStrategy =>

  protected val askOrdering: PriceOrdering[AskOrder]

  protected val bidOrdering: PriceOrdering[BidOrder]

  protected var orderBook: immutable.Set[Order]

  def askOrderBook: immutable.Seq[AskOrder] = {
    val askOrders = orderBook.collect { case order : AskOrder => order }
    askOrders.to[immutable.Seq].sorted(askOrdering)
  }

  def bidOrderBook: immutable.Seq[BidOrder] = {
    val bidOrders = orderBook.collect { case order : BidOrder => order }
    bidOrders.to[immutable.Seq].sorted(bidOrdering)
  }

  protected def crosses(incomingOrder: Order, existingOrder: Order): Boolean = {
    (incomingOrder, existingOrder) match {
      case (ask: AskOrder, bid: BidOrder) => ask.price <= bid.price
      case (bid: BidOrder, ask: AskOrder) => bid.price >= ask.price
    }
  }

  def fill(incomingOrder: Order): Option[immutable.Iterable[FilledOrder]] = {

    /** Accumulate some filled orders
      *
      * @param incomingOrder
      * @param accum
      * @param existingOrders CALL BY NAME!
      * @return
      */
    @tailrec
    def accumulate(incomingOrder: Order,
                   accum: immutable.Queue[FilledOrder],
                   existingOrders: => immutable.Seq[Order]): immutable.Queue[FilledOrder] = {
      existingOrders.headOption match {
        case Some(existingOrder) if crosses(incomingOrder, existingOrder) =>
          orderBook -= existingOrder  // SIDE EFFECT!
          val residualQuantity = incomingOrder.quantity - existingOrder.quantity
          val price = formPrice(incomingOrder, existingOrder)
          val quantity = math.min(incomingOrder.quantity, existingOrder.quantity)
          if (residualQuantity < 0) {
            val filledOrder = TotalFilledOrder(incomingOrder.issuer, existingOrder.issuer, price, quantity, 1, incomingOrder.tradable)
            // add residualBidOrder back into bidOrderBook!
            val residualOrder = existingOrder.split(-residualQuantity)
            orderBook += residualOrder  // SIDE EFFECT!
            accum.enqueue(filledOrder)
          } else if (residualQuantity == 0) {  // no rationing for incoming order!
              val filledOrder = TotalFilledOrder(incomingOrder.issuer, existingOrder.issuer, price, quantity, 1, incomingOrder.tradable)
              accum.enqueue(filledOrder)
          } else {  // incoming order is larger than existing order and will be rationed!
            val filledOrder = PartialFilledOrder(incomingOrder.issuer, existingOrder.issuer, price,
              quantity, 1, incomingOrder.tradable)
            val residualOrder = incomingOrder.split(residualQuantity)
            accumulate(residualOrder, accum.enqueue(filledOrder), existingOrders)
          }
        case _ => // bidOrderBook is empty or incoming ask does not cross existing bid.
          orderBook += incomingOrder  // SIDE EFFECT!
          accum
      }
    }

    incomingOrder match {
      case order: AskOrder =>
        val filledOrders = accumulate(order, immutable.Queue.empty[FilledOrder], bidOrderBook)
        if (filledOrders.isEmpty) None else Some(filledOrders)
      case order: BidOrder =>
        val filledOrders = accumulate(order, immutable.Queue.empty[FilledOrder], askOrderBook)
        if (filledOrders.isEmpty) None else Some(filledOrders)
    }

  }

}
