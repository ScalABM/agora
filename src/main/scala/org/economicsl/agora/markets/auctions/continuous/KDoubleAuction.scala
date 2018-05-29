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
package org.economicsl.agora.markets.auctions.continuous

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.orderbooks.OrderBook
import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricingRule
import org.economicsl.agora.markets.tradables.{Quantity, Tradable}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


/** Class implementing a k-Double Auction as described in [[http://www.sciencedirect.com/science/article/pii/002205318990121X Satterthwaite and Williams (JET, 1989)]].
  *
  * @param k the value of `k` determines the division of gains from trade between the buyer and the seller. A value of
  *          `k` in (0, 1) implies that both the buyer and the seller have influence over the price at which trade
  *          occurs. If `k=0`, then the seller sets the price unilaterally; at the other extreme, if `k=1`, the buyer
  *          sets the price unilaterally.
  */
class KDoubleAuction(askOrderBook: OrderBook[LimitAskOrder with Persistent with Quantity],
                     askOrderMatchingRule: (LimitAskOrder with Quantity, OrderBook[LimitBidOrder with Persistent with Quantity]) => Option[LimitBidOrder with Persistent with Quantity],
                     bidOrderBook: OrderBook[LimitBidOrder with Persistent with Quantity],
                     bidOrderMatchingRule: (LimitBidOrder with Quantity, OrderBook[LimitAskOrder with Persistent with Quantity]) => Option[LimitAskOrder with Persistent with Quantity],
                     val k: Double,
                     val tradable: Tradable)
  extends DoubleAuction[LimitAskOrder with Quantity, LimitBidOrder with Quantity] {

  require(0 <= k && k <= 1, "The value of k must be in the unit interval (i.e., [0, 1]).")

  /** Fill an incoming `LimitAskOrder`.
    *
    * @param order a `LimitAskOrder` instance.
    * @return
    */
  final def fill(order: LimitAskOrder with Quantity): Option[Fill] = {
    val matchingOrders = askOrderMatchingRule(order, bidOrderBook) // eventually this will return an iterable?
    matchingOrders.foreach(matchingOrder => orderBook = orderBook - matchingOrder.issuer)// SIDE EFFECT!
    matchingOrders.map { matchingOrder =>
      val price = pricingRule(order, matchingOrder)
      val quantity = math.min(order.quantity, matchingOrder.quantity) // not dealing with residual orders!
      performance.addValue(surplus(order, matchingOrder))  // SIDE EFFECT!
      new Fill(order, matchingOrder, price, quantity)
    }
  }

  /** Fill an incoming `LimitBidOrder`.
    *
    * @param order a `LimitBidOrder` instance.
    * @return
    */
  final def fill(order: LimitBidOrder with Quantity): Option[Fill] = {
    val matchingOrders = bidOrderMatchingRule(order, askOrderBook) // eventually this will return an iterable?
    matchingOrders.foreach(matchingOrder => askOrderBook = askOrderBook - matchingOrder.issuer)// SIDE EFFECT!
    matchingOrders.map { matchingOrder =>
      val price = pricingRule(order, matchingOrder)
      val quantity = math.min(order.quantity, matchingOrder.quantity) // not dealing with residual orders!
      performance.addValue(surplus(order, matchingOrder))  // SIDE EFFECT!
      new Fill(order, matchingOrder, price, quantity)
    }
  }

  private[this] val askOrderPricingRule = {
    WeightedAveragePricingRule[LimitAskOrder with Quantity, LimitBidOrder with Persistent with Quantity](1-k)
  }

  private[this] val bidOrderPricingRule = {
    WeightedAveragePricingRule[LimitBidOrder with Quantity, LimitAskOrder with Persistent with Quantity](k)
  }

}


object KDoubleAuction {

  /** Create an instance of a `KDoubleAuction`.
    *
    * @param askOrderBook
    * @param askOrderMatchingRule
    * @param bidOrderBook
    * @param bidOrderMatchingRule
    * @param k
    * @param tradable
    * @return an instance of a `KDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderBook: OrderBook[LimitAskOrder with Persistent with Quantity],
            askOrderMatchingRule: (LimitAskOrder with Quantity, OrderBook[LimitBidOrder with Persistent with Quantity]) => Option[LimitBidOrder with Persistent with Quantity],
            bidOrderBook: OrderBook[LimitBidOrder with Persistent with Quantity],
            bidOrderMatchingRule: (LimitBidOrder with Quantity, OrderBook[LimitAskOrder with Persistent with Quantity]) => Option[LimitAskOrder with Persistent with Quantity],
            k: Double,
            tradable: Tradable): KDoubleAuction = {
    new KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, k, tradable)
  }

  /** Create an instance of a `KDoubleAuction`.
    *
    * @param askOrderMatchingRule
    * @param bidOrderMatchingRule
    * @param k
    * @param tradable
    * @return an instance of a `KDoubleAuction` for a particular `Tradable`
    */
  def apply(askOrderMatchingRule: (LimitAskOrder with Quantity, OrderBook[LimitBidOrder with Persistent with Quantity]) => Option[LimitBidOrder with Persistent with Quantity],
            bidOrderMatchingRule: (LimitBidOrder with Quantity, OrderBook[LimitAskOrder with Persistent with Quantity]) => Option[LimitAskOrder with Persistent with Quantity],
            k: Double,
            tradable: Tradable): KDoubleAuction = {
    val askOrderBook = OrderBook[LimitAskOrder with Persistent with Quantity](tradable)
    val bidOrderBook = OrderBook[LimitBidOrder with Persistent with Quantity](tradable)
    new KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, k, tradable)
  }

}