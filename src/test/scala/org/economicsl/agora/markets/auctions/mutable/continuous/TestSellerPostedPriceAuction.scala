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
package org.economicsl.agora.markets.auctions.mutable.continuous

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.matching.FindFirstAcceptableOrder
import org.economicsl.agora.markets.auctions.mutable.orderbooks.SortedOrderBook
import org.economicsl.agora.markets.auctions.pricing.BestLimitPricingFunction
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


case class TestSellerPostedPriceAuction(tradable: Tradable)(implicit ordering: Ordering[LimitAskOrder])
  extends SellerPostedPriceAuction[LimitBidOrder, LimitAskOrder] {

  val matchingFunction = new FindFirstAcceptableOrder[LimitBidOrder, LimitAskOrder]()

  val pricingFunction = new BestLimitPricingFunction[LimitBidOrder, LimitAskOrder]()

  def cancel(order: LimitAskOrder): Option[LimitAskOrder] = orderBook.remove(order.uuid)

  def fill(order: LimitBidOrder): Option[Fill] = matchingFunction(order, orderBook) match {
    case Some(askOrder) =>
      orderBook.remove(askOrder.uuid)  // SIDE EFFECT!
      val fillPrice = pricingFunction(order, askOrder)
      val fillQuantity = math.min(askOrder.quantity, order.quantity)  // residual orders are not stored!
      Some(new Fill(order.issuer, askOrder.issuer, fillPrice, fillQuantity, tradable))
    case None => None
  }

  def place(order: LimitAskOrder): Unit = orderBook.add(order)

  protected[auctions] val orderBook = SortedOrderBook[LimitAskOrder](tradable)(ordering)

}
