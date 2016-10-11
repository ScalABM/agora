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
package markets.auctions

import markets.matching.MatchingFunction
import markets.orderbooks
import markets.pricing.PricingFunction
import markets.tradables.Tradable
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


/** Class used to test `DoubleAuctionMarket`.
  *
  * @param matchingFunction
  * @param pricingFunction
  * @param tradable
  * @note the order in which matching, price formation, and add/remove of orders is done is application specific.
  */
class TestDoubleAuction(val matchingFunction: MatchingFunction[AskOrder, BidOrder],
                        val pricingFunction: PricingFunction[AskOrder, BidOrder],
                        val tradable: Tradable)
  extends DoubleAuction {

  def fill(order: AskOrder): Option[Fill] = {
    matchingFunction(order, bidOrderBook) match {
      case Some((askOrder, bidOrder)) =>
        askOrderBook.remove(askOrder.uuid)  // SIDE EFFECT!
        val executionPrice = pricingFunction(askOrder, bidOrder)
        val fill = new Fill(bidOrder.issuer, askOrder.issuer, executionPrice, 1, tradable)
        Some(fill)
      case None =>
        askOrderBook.add(order)  // SIDE EFFECT!
        None
    }
  }

  def fill(order: BidOrder): Option[Fill] = {
    matchingFunction(order, askOrderBook) match {
      case Some((bidOrder, askOrder)) =>
        bidOrderBook.remove(bidOrder.uuid)  // SIDE EFFECT!
        val executionPrice = pricingFunction(bidOrder, askOrder)
        val fill = new Fill(bidOrder.issuer, askOrder.issuer, executionPrice, 1, tradable)
        Some(fill)
      case None =>
        bidOrderBook.add(order)  // SIDE EFFECT!
        None
    }
  }

  protected[auctions] val askOrderBook = orderbooks.mutable.OrderBook[AskOrder](tradable)

  protected[auctions] val bidOrderBook = orderbooks.mutable.OrderBook[BidOrder](tradable)

}
