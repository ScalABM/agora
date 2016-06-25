package markets.auctions

import markets.auctions.orderbooks.PriorityOrderBook
import markets.orders.{AskOrder, BidOrder, Order}

import scala.annotation.tailrec
import scala.collection.immutable.Queue


trait TwoSidedAuction {

  def askOrderBook: PriorityOrderBook[AskOrder]

  def bidOrderBook: PriorityOrderBook[BidOrder]

  /** Rule specifying the transaction price between two orders.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite order book.
    * @return the price at which a trade between the two orders will take place.
    */
  def formPrice(incoming: Order, existing: Order): Long

  /** Rule specifying the transaction quantity between two orders.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite order book.
    * @return the quantity at which a trade between the two orders will take place.
    */
  def formQuantity(incoming: Order, existing: Order): Long = {
    math.min(incoming.quantity, existing.quantity)
  }

  @tailrec
  protected final def accumulateAskOrders(incoming: BidOrder,
                                          matchings: Queue[Matching]): Queue[Matching] = {
    askOrderBook.peek match {
      case Some(askOrder) if incoming.crosses(askOrder) =>
        askOrderBook.poll()  // SIDE EFFECT!
      val residualQuantity = incoming.quantity - askOrder.quantity
        val price = formPrice(incoming, askOrder)
        val quantity = formQuantity(incoming, askOrder)
        if (residualQuantity < 0) {  // incoming order is smaller than existing order
          val (_, residualAskOrder) = askOrder.split(-residualQuantity)
          val matching = Matching(askOrder, incoming, price, quantity, Some(residualAskOrder), None)
          askOrderBook.add(residualAskOrder)  // SIDE EFFECT!
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
        bidOrderBook.add(incoming)  // SIDE EFFECT!
        matchings
    }
  }

  @tailrec
  protected final def accumulateBidOrders(incoming: AskOrder,
                                          matchings: Queue[Matching]): Queue[Matching] = {
    bidOrderBook.peek match {
      case Some(bidOrder) if incoming.crosses(bidOrder) =>
        bidOrderBook.poll()  // SIDE EFFECT!
      val residualQuantity = incoming.quantity - bidOrder.quantity
        val price = formPrice(incoming, bidOrder)
        val quantity = formQuantity(incoming, bidOrder)
        if (residualQuantity < 0) { // incoming order is smaller than existing order!
          val (_, residualBidOrder) = bidOrder.split(-residualQuantity)
          val matching = Matching(incoming, bidOrder, price, quantity, None, Some(residualBidOrder))
          bidOrderBook.add(residualBidOrder)  // SIDE EFFECT!
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
        askOrderBook.add(incoming)  // SIDE EFFECT!
        matchings
    }
  }

}
