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
import markets.orders.{AskOrder, BidOrder, Order}
import markets.fills.{Fill, PartialFill, TotalFill}
import markets.orders.orderings.PriceOrdering

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

  def fill(incomingOrder: Order): Option[immutable.Iterable[Fill]] = {

    incomingOrder match {
      case order: AskOrder =>
        val fills = accumulate(order, immutable.Queue.empty[Fill], bidOrderBook)
        if (fills.isEmpty) None else Some(fills)
      case order: BidOrder =>
        val fills = accumulate(order, immutable.Queue.empty[Fill], askOrderBook)
        if (fills.isEmpty) None else Some(fills)
    }

  }

  /** Accumulate some fills orders
    *
    * @param incomingOrder
    * @param fills
    * @param existingOrders CALL BY NAME!
    * @return
    */
  @tailrec
  private[this] def accumulate(incomingOrder: Order,
                               fills: immutable.Queue[Fill],
                               existingOrders: => immutable.Seq[Order]): immutable.Queue[Fill] = {
    existingOrders.headOption match {
      case Some(existingOrder) if crosses(incomingOrder, existingOrder) =>
        orderBook -= existingOrder  // SIDE EFFECT!
        val residualQuantity = incomingOrder.quantity - existingOrder.quantity
        val price = formPrice(incomingOrder, existingOrder)
        val quantity = math.min(incomingOrder.quantity, existingOrder.quantity)
        if (residualQuantity < 0) {
          val fill = TotalFill(incomingOrder.issuer, existingOrder.issuer, price, quantity, 1)
          // add residualOrder back into orderBook!
          val residualOrder = existingOrder.split(-residualQuantity)
          orderBook += residualOrder  // SIDE EFFECT!
          fills.enqueue(fill)
        } else if (residualQuantity == 0) {  // no rationing for incoming order!
        val fill = TotalFill(incomingOrder.issuer, existingOrder.issuer, price, quantity, 1)
          fills.enqueue(fill)
        } else {  // incoming order is larger than existing order and will be rationed!
        val fill = PartialFill(incomingOrder.issuer, existingOrder.issuer, price, quantity, 1)
          val residualOrder = incomingOrder.split(residualQuantity)
          accumulate(residualOrder, fills.enqueue(fill), existingOrders)
        }
      case _ => // existingOrders is empty or incoming order does not cross best existing order.
        orderBook += incomingOrder  // SIDE EFFECT!
        fills
    }
  }

}
