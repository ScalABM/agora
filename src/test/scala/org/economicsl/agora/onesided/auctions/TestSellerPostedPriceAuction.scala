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

import java.util.UUID

import org.economicsl.agora.{orderbooks, Fill}
import org.economicsl.agora.onesided.matching.BestPriceMatchingFunction
import org.economicsl.agora.onesided.pricing.BestLimitPricingFunction
import org.economicsl.agora.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.tradables.orders.bid.LimitBidOrder
import org.economicsl.agora.tradables.Tradable

import scala.collection.mutable


case class TestSellerPostedPriceAuction(tradable: Tradable)(implicit ordering: Ordering[LimitAskOrder])
  extends SellerPostedPriceAuction[LimitBidOrder, orderbooks.mutable.SortedOrderBook[LimitAskOrder, mutable.Map[UUID, LimitAskOrder]], LimitAskOrder] {

  val matchingFunction = new BestPriceMatchingFunction[LimitBidOrder, LimitAskOrder]()

  val pricingFunction = new BestLimitPricingFunction[LimitBidOrder, LimitAskOrder]()

  def fill(order: LimitBidOrder): Option[Fill] = matchingFunction(order, orderBook) match {
    case Some(askOrder) =>
      orderBook.remove(askOrder.uuid)  // SIDE EFFECT!
      val fillPrice = pricingFunction(order, askOrder)
      val fillQuantity = math.min(askOrder.quantity, order.quantity)  // residual orders are not stored!
      Some(new Fill(order.issuer, askOrder.issuer, fillPrice, fillQuantity, tradable))
    case None => None
  }

  protected[auctions] val orderBook = orderbooks.mutable.SortedOrderBook[LimitAskOrder](tradable)(ordering)

}
