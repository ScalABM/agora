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
package markets.twosided.auctions.continuous

import markets.Fill
import markets.onesided.auctions.{TestBuyerPostedPriceAuction, TestSellerPostedPriceAuction}
import markets.twosided.matching
import markets.tradables.Tradable
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


/** Class used to test `ContinuousDoubleAuction`.
  *
  * @param matchingFunction
  * @param pricingFunction
  */
case class TestPostedPriceAuction[A <: AskOrder, B <: BidOrder](matchingFunction: matching.MatchingFunction[A, B],
                                                                pricingFunction: matching.PricingFunction[A, B],
                                                                tradable: Tradable)
  extends PostedPrice[A, B] {

  def fill(order: A): Option[Fill] = buyerPostedPriceAuction.fill(order) match {
    case result @ Some(fill) => result
    case None => sellerPostedPriceAuction.place(order); None  // SIDE EFFECT!
  }

  def fill(order: B): Option[Fill] = sellerPostedPriceAuction.fill(order) match {
    case result @ Some(fill) => result
    case None => buyerPostedPriceAuction.place(order); None  // SIDE EFFECT!
  }

  protected val buyerPostedPriceAuction = {
    TestBuyerPostedPriceAuction(matchingFunction.askOrderMatchingFunction, pricingFunction.askOrderPricingFunction, tradable)
  }

  protected  val sellerPostedPriceAuction = {
    TestSellerPostedPriceAuction(matchingFunction.bidOrderMatchingFunction, pricingFunction.bidOrderPricingFunction, tradable)
  }

}