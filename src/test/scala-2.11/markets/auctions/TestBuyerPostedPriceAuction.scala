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


case class TestBuyerPostedPriceAuction(matchingFunction: MatchingFunction[BidOrder, AskOrder],
                                       pricingFunction: PricingFunction[BidOrder, AskOrder],
                                       tradable: Tradable)
  extends BuyerPostedPriceAuction[AskOrder, BidOrder] {

  def fill(order: AskOrder): Option[Fill] = {
    matchingFunction(order, orderBook) match {
      case Some((askOrder, bidOrder)) =>
        orderBook.remove(bidOrder.uuid)  // SIDE EFFECT!
        val fillPrice = pricingFunction(bidOrder, askOrder)
        val fillQuantity = math.min(askOrder.quantity, bidOrder.quantity)  // residual orders are not stored!
        Some(new Fill(bidOrder.issuer, askOrder.issuer, fillPrice, fillQuantity, tradable))
      case None => None
    }
  }

  protected[auctions] val orderBook = orderbooks.parallel.mutable.OrderBook[BidOrder](tradable)

}
