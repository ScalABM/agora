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
package org.economicsl.agora.markets.auctions.concurrent.continuous

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.concurrent.orderbooks.{BidOrderBook, GenOrderBook, ParBidOrderBook}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, Quantity, Tradable}


/** Class defining a `BuyerPostedPriceAuction`.
  *
  * @tparam A the type of `AskOrder` instances that should be filled by the `BuyerPostedPriceAuction`.
  * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
  */
abstract class BuyerPostedPriceAuction[A <: AskOrder with LimitPrice with Quantity,
                                       BB <: GenOrderBook[B], B <: BidOrder with LimitPrice with Persistent with Quantity]
                                      (matchingRule: (A, BB) => Option[B], pricingRule: (A, B) => Price)
  extends PostedPriceAuction[A, B] {

  final def fill(order: A): Option[Fill] = {
    val matchingOrders = matchingRule(order, orderBook) // eventually this will return an iterable?
    matchingOrders.foreach(matchingOrder => orderBook.remove(matchingOrder.issuer)) // SIDE EFFECT!
    matchingOrders.map { matchingOrder =>
      val price = pricingRule(order, matchingOrder)
      val quantity = math.min(order.quantity, matchingOrder.quantity) // not dealing with residual orders!
      performance.addValue(surplus(order, matchingOrder))  // SIDE EFFECT!
      new Fill(order, matchingOrder, price, quantity)
    }
  }

  /** Return the surplus generated by a trade.
    *
    * @param askOrder an instance of `AskOrder with LimitPrice with Quantity`.
    * @param bidOrder an instance of `BidOrder with LimitPrice with Quantity`.
    * @return surplus as measured by the difference between `bidOrder` and `askOrder` `limit` prices.
    */
  protected def surplus(askOrder: A, bidOrder: B): Double = bidOrder.limit.value - askOrder.limit.value

  protected def orderBook: BB

}


object BuyerPostedPriceAuction {

  /** Create an instance of a `BuyerPostedPriceAuction`.
    *
    * @param matchingRule
    * @param pricingRule
    * @tparam A
    * @tparam B
    * @return an instance of a `BuyerPostedPriceAuction`.
    */
  def apply[A <: AskOrder with LimitPrice with Quantity,
            B <: BidOrder with LimitPrice with Persistent with Quantity]
           (matchingRule: (A, BidOrderBook[B]) => Option[B], pricingRule: (A, B) => Price, tradable: Tradable)
           : BuyerPostedPriceAuction[A, BidOrderBook[B], B] = {

    new BuyerPostedPriceAuction(matchingRule, pricingRule) {
      protected val orderBook: BidOrderBook[B] = BidOrderBook[B](tradable)
    }

  }

  /** Create an instance of a `BuyerPostedPriceAuction`.
    *
    * @param matchingRule
    * @param pricingRule
    * @tparam A
    * @tparam B
    * @return an instance of a `BuyerPostedPriceAuction`.
    */
  def apply[A <: AskOrder with LimitPrice with Quantity,
            B <: BidOrder with LimitPrice with Persistent with Quantity]
           (matchingRule: (A, ParBidOrderBook[B]) => Option[B], pricingRule: (A, B) => Price, tradable: Tradable)
           : BuyerPostedPriceAuction[A, ParBidOrderBook[B], B] = {

    new BuyerPostedPriceAuction(matchingRule, pricingRule) {
      protected val orderBook: ParBidOrderBook[B] = ParBidOrderBook[B](tradable)
    }

  }

}