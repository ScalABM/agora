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
package markets.auctions.twosided.continuous

import markets.auctions.onesided
import markets.auctions.Fill
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


/** Class used to test `ContinuousDoubleAuction`.
  *
  * @param buyerPostedPriceAuction
  * @param sellerPostedPriceAuction
  */
case class TestPostedPriceAuction[A <: AskOrder, B <: BidOrder](buyerPostedPriceAuction: onesided.BuyerPostedPriceAuction[A, B],
                                                                sellerPostedPriceAuction: onesided.SellerPostedPriceAuction[A, B])
  extends PostedPrice[A, B] {

  def fill(order: A): Option[Fill] = buyerPostedPriceAuction.fill(order) match {
    case result @ Some(fill) => result
    case None => sellerPostedPriceAuction.place(order); None  // SIDE EFFECT!
  }

  def fill(order: B): Option[Fill] = sellerPostedPriceAuction.fill(order) match {
    case result @ Some(fill) => result
    case None => buyerPostedPriceAuction.place(order); None  // SIDE EFFECT!
  }

}


object TestPostedPriceAuction {

  /*def apply[A <: AskOrder, B <: BidOrder](matchingFunction: matching.twosided.MatchingFunction[A, B],
                                          pricingFunction: pricing.twosided.PricingFunction[A, B]): ContinuousDoubleAuction[A, B] = {
    val buyerPostedPriceAuction = ???
    val sellerPostedPriceAuction = ???
    ???
  }*/

}