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
package org.economicsl.agora.onesided.auctions

import org.economicsl.agora.orderbooks.OrderBookLike
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder
import org.economicsl.agora.{Fill, orderbooks}


/** Abstract class defining the interface for a `BuyerPostedPriceAuction`.
  *
  * @tparam A the type of `AskOrder` instances that should be filled by the `BuyerPostedPriceAuction`.
  * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
  */
class BuyerPostedPriceAuction[A <: AskOrder, B <: BidOrder](bidOrderBook: OrderBookLike[B],
                                                            matchingRule: (A, OrderBookLike[B]) => Option[B],
                                                            pricingRule: (A, B) => Long)
  extends PostedPriceAuction[A, orderbooks.OrderBookLike[B], B] {

  def fill(order: A): Option[Fill] = ???

  def cancel(order: B): Option[B] = ???

  def place(order: B): Unit = ???

  protected val orderBook: OrderBookLike[B] = bidOrderBook

}


object BuyerPostedPriceAuction {

  /** Create an instance of a `BuyerPostedPriceAuction`.
    *
    * @param matchingRule
    * @param pricingRule
    * @param tradable
    * @tparam A
    * @tparam B
    * @return an instance of a `BuyerPostedPriceAuction`.
    */
  def apply[A <: AskOrder, B <: BidOrder](matchingRule: (A, OrderBookLike[B]) => Option[B],
                                          pricingRule: (A, B) => Long,
                                          tradable: Tradable): BuyerPostedPriceAuction[A, B] = {
    new DefaultImpl[A, B](matchingRule, pricingRule, tradable)
  }

  /** Priavte, default implementation of a `BuyerPostedPriceAuction`.
    *
    * @param matchingRule
    * @param pricingRule
    * @param tradable
    * @tparam A the type of `AskOrder` instances that should be filled by the `BuyerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
    */
  private[this] case class DefaultImpl[A <: AskOrder, B <: BidOrder](matchingRule: (A, OrderBookLike[B]) => Option[B],
                                                                     pricingRule: (A, B) => Long,
                                                                     tradable: Tradable)
    extends BuyerPostedPriceAuction[A, B](matchingRule, pricingRule, tradable) {

    def cancel(order: B): Option[B] = orderBook.remove(order.uuid)

    /** Fill an order.
      *
      * @param order
      * @return
      */
    final def fill(order: A): Option[Fill] = {
      val matchingBidOrder = matchingRule(order, orderBook)  // eventually this will return an iterable!
      matchingBidOrder.foreach(bidOrder => orderBook.remove(bidOrder.uuid))  // SIDE EFFECT!
      matchingBidOrder.map { bidOrder =>
        val price = pricingRule(order, bidOrder)
        val quantity = math.min(order.quantity, bidOrder.quantity)  // not dealing with residual orders!
        new Fill(bidOrder.issuer, order.issuer, price, quantity, tradable)
      }
    }

    def place(order: B): Unit = orderBook.add(order)

    protected val orderBook = orderbooks.mutable.OrderBook[B](tradable)

  }

}