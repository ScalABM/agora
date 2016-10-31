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
package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.matching.FindFirstAcceptableOrder
import org.economicsl.agora.markets.auctions.pricing.BestLimitPricingFunction
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


case class TestBuyerPostedPriceAuction(tradable: Tradable)(implicit ordering: Ordering[LimitBidOrder])
  extends BuyerPostedPriceAuction[LimitAskOrder, LimitBidOrder] {

  val matchingFunction = new FindFirstAcceptableOrder[LimitAskOrder, LimitBidOrder]()

  val pricingFunction = new BestLimitPricingFunction[LimitAskOrder, LimitBidOrder]()

  def cancel(order: LimitBidOrder): Option[LimitBidOrder] = orderBook.remove(order.uuid)

  def fill(order: LimitAskOrder): Option[Fill] = matchingFunction(order, orderBook) match {
    case Some(bidOrder) =>
      orderBook.remove(bidOrder.uuid)  // SIDE EFFECT!
      val fillPrice = pricingFunction(order, bidOrder)
      val fillQuantity = math.min(order.quantity, bidOrder.quantity)  // residual orders are not stored!
      Some(new Fill(bidOrder.issuer, order.issuer, fillPrice, fillQuantity, tradable))
    case None => None
  }

  def place(order: LimitBidOrder): Unit = orderBook.add(order)

  protected[auctions] val orderBook = orderbooks.mutable.SortedOrderBook[LimitBidOrder](tradable)(ordering)

}
