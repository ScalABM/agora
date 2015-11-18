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

import markets.orders._

import scala.annotation.tailrec
import scala.collection.immutable


trait ContinuousDoubleAuctionLike extends MatchingEngineLike {

  // Favor use of immutable var over mutable val
  var askOrderBook: immutable.Iterable[AskOrderLike]

  var bidOrderBook: immutable.Iterable[BidOrderLike]

  val referencePrice: Option[Double] = None

  def fillIncomingOrder(incoming: OrderLike): Option[immutable.Iterable[FilledOrderLike]] = {
    incoming match {
      case incoming: AskOrderLike => fillIncomingAskOrder(incoming)
      case incoming: BidOrderLike => fillIncomingBidOrder(incoming)
    }
  }

  def fillIncomingAskOrder(incoming: AskOrderLike): Option[immutable.Iterable[FilledOrderLike]] = {

    /**
      *
      * @param ask
      * @param accum Use Queue because want FIFO behavior and O(1) append.
      * @return
      * @note SIDE EFFECT!
      * @todo where is the timestamp going to come from?
      *
      */
    @tailrec
    def accumulateFilledOrders(ask: AskOrderLike,
                               accum: immutable.Queue[FilledOrderLike]): immutable.Queue[FilledOrderLike] = {
      bidOrderBook.headOption match {
        case Some(bid) if bid.price >= ask.price =>
          bidOrderBook = bidOrderBook.tail  // removes bid from bidOrderBook! SIDE EFFECT!
          val excessDemand = bid.quantity - ask.quantity
          val counterParties = (ask.issuer, bid.issuer)
          val price = bid.price  // @todo abstract over the price formation rule!
          if (excessDemand > 0) {
            val quantity = ask.quantity  // no rationing for incoming ask
            val filledOrder = TotalFilledOrder(counterParties, price, quantity, 1, bid.tradable)
            // add residualBidOrder back into bidOrderBook! SIDE EFFECT!
            val residualBidOrder = bid.split(excessDemand)
            bidOrderBook = bidOrderBook ++ immutable.Iterable(residualBidOrder)
            accum.enqueue(filledOrder)
          } else if (excessDemand == 0) {  // ask quantity matches bid quantity
          val quantity = ask.quantity // no rationing for incoming ask!
          val filledOrder = TotalFilledOrder(counterParties, price, quantity, 1, bid.tradable)
            accum.enqueue(filledOrder)
          } else {  // incoming ask is larger than bid and will be rationed!
          val quantity = bid.quantity  // rationing!
          val filledOrder = PartialFilledOrder(counterParties, price, quantity, 1, bid.tradable)
            val residualAskOrder = ask.split(-excessDemand)
            accumulateFilledOrders(residualAskOrder, accum.enqueue(filledOrder))
          }
        case _ => // bidOrderBook is empty or incoming ask does not cross existing bid. SIDE EFFECT!
          askOrderBook = askOrderBook ++ immutable.Iterable(ask)
          accum
      }
    }
    val filledOrders = accumulateFilledOrders(incoming, immutable.Queue.empty[FilledOrderLike])
    if (filledOrders.isEmpty) None else Some(filledOrders)
  }

  def fillIncomingBidOrder(incoming: BidOrderLike): Option[immutable.Iterable[FilledOrderLike]] = {

    /**
      *
      * @param bid
      * @param accum Use Queue because want FIFO behavior and O(1) append.
      * @return
      * @note SIDE EFFECT!
      * @todo where is the timestamp going to come from?
      *
      */
    @tailrec
    def accumulateFilledOrders(bid: BidOrderLike,
                               accum: immutable.Queue[FilledOrderLike]): immutable.Queue[FilledOrderLike] = {
      askOrderBook.headOption match {
        case Some(ask) if bid.price >= ask.price =>
          askOrderBook = askOrderBook.tail // removes ask from askOrderBook! SIDE EFFECT!
          val excessDemand = bid.quantity - ask.quantity
          val counterParties = (ask.issuer, bid.issuer)
          val price = ask.price  // @todo abstract over the price formation rule!
          if (excessDemand < 0) {
          val quantity = bid.quantity  // no rationing for incoming bid
          val filledOrder = TotalFilledOrder(counterParties, price, quantity, 1, bid.tradable)
            // add residualAskOrder back into askOrderBook! SIDE EFFECT!
            val residualAskOrder = ask.split(-excessDemand)
            askOrderBook = askOrderBook ++ immutable.Iterable(residualAskOrder)
            accum.enqueue(filledOrder)
          } else if (excessDemand == 0) {  // bid quantity matches ask quantity
          val quantity = bid.quantity // no rationing for incoming bid!
          val filledOrder = TotalFilledOrder(counterParties, price, quantity, 1, bid.tradable)
            accum.enqueue(filledOrder)
          } else {
          val quantity = ask.quantity  // incoming bid is rationed!
          val filledOrder = PartialFilledOrder(counterParties, price, quantity, 1, bid.tradable)
            val residualBidOrder = bid.split(excessDemand)
            accumulateFilledOrders(residualBidOrder, accum.enqueue(filledOrder))
          }
        case _ => // askOrderBook is empty or incoming bid does not cross existing ask. SIDE EFFECT!
          bidOrderBook = bidOrderBook ++ immutable.Iterable(bid)
          accum
      }
    }
    val filledOrders = accumulateFilledOrders(incoming, immutable.Queue.empty[FilledOrderLike])
    if (filledOrders.isEmpty) None else Some(filledOrders)
  }

  def formPrice(incoming: OrderLike, existing: OrderLike): Long = {
    (incoming, existing) match {
      case _: (LimitAskOrder, LimitBidOrder) => ???
      case _: (MarketAskOrder, LimitBidOrder) => ???
      case _: (LimitAskOrder, MarketBidOrder) => ???
      case _: (MarketAskOrder, MarketBidOrder) => ???
    }
  }
}
