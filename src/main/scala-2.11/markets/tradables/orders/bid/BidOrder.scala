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
package markets.tradables.orders.bid

import markets.tradables.orders.Order
import markets.tradables.Quantity


/** Trait defining an order to buy a `Tradable` object. */
trait BidOrder extends Order with Quantity


object BidOrder {

  implicit val ordering: Ordering[BidOrder] = new DefaultOrdering

  val priority: Ordering[BidOrder] = ordering.reverse

  /** Class implementing an ordering over various `BidOrder` types. */
  private[this] class DefaultOrdering extends Ordering[BidOrder] {

    def compare(bidOrder1: BidOrder, bidOrder2: BidOrder): Int = (bidOrder1, bidOrder2) match {
      case (_: MarketBidOrder, _: LimitBidOrder) => -1
      case (limitOrder1: LimitBidOrder, limitOrder2: LimitBidOrder) =>
        LimitBidOrder.ordering.compare(limitOrder1, limitOrder2)
      case (_: LimitBidOrder, _: MarketBidOrder) => 1
      case (marketOrder1: MarketBidOrder, marketOrder2: MarketBidOrder) =>
        MarketBidOrder.ordering.compare(marketOrder1, marketOrder2)
    }

  }

}