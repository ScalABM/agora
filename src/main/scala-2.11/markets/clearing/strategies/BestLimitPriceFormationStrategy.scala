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
package markets.clearing.strategies

import markets.clearing.engines.CDAMatchingEngineLike
import markets.orders.limit.{LimitAskOrder, LimitBidOrder, LimitOrderLike}
import markets.orders.market.{MarketAskOrder, MarketBidOrder, MarketOrderLike}
import markets.orders.Order

import scala.collection.immutable


trait BestLimitPriceFormationStrategy extends PriceFormationStrategy {
  this: CDAMatchingEngineLike =>

  protected var mostRecentPrice: Long

  def bestLimitOrder(orderBook: immutable.Iterable[Order]): Option[Order] = {
    orderBook.find(order => order.isInstanceOf[LimitOrderLike])
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

      // Handle incoming limit orders
      case (incoming: LimitOrderLike, existing: LimitOrderLike) =>
        mostRecentPrice = existing.price
        mostRecentPrice
      case (incoming: LimitAskOrder, existing: MarketBidOrder) =>
        bestLimitOrder(bidOrderBook) match {
          case Some(limitOrder) =>
            val possiblePrices = immutable.Seq(incoming.price, limitOrder.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.max
            mostRecentPrice
          case None =>
            mostRecentPrice = incoming.price
            mostRecentPrice
        }
      case (incoming: LimitBidOrder, existing: MarketAskOrder) =>
        bestLimitOrder(askOrderBook) match {
          case Some(limitOrder) =>
            val possiblePrices = immutable.Seq(incoming.price, limitOrder.price, mostRecentPrice)
            mostRecentPrice = possiblePrices.min
            mostRecentPrice
          case None =>
            mostRecentPrice = incoming.price
            mostRecentPrice
        }

      // Handle incoming market orders
      case (incoming: MarketOrderLike, existing: LimitOrderLike) =>
        mostRecentPrice = existing.price
        mostRecentPrice
      case (incoming: MarketAskOrder, existing: MarketBidOrder) =>
        bestLimitOrder(bidOrderBook) match {
          case Some(limitOrder) =>
            mostRecentPrice = math.max(limitOrder.price, mostRecentPrice)
            mostRecentPrice
          case None => mostRecentPrice
        }
      case (incoming: MarketBidOrder, existing: MarketAskOrder) =>
        bestLimitOrder(askOrderBook) match {
          case Some(limitOrder) =>
            mostRecentPrice = math.min(limitOrder.price, mostRecentPrice)
            mostRecentPrice
          case None => mostRecentPrice
        }

    }
  }

}
