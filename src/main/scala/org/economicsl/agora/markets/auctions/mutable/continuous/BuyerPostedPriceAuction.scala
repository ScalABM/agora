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

import org.economicsl.agora.markets.auctions.mutable.orderbooks
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, Quantity}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** Class defining a `BuyerPostedPriceAuction`.
  *
  * @tparam A the type of `AskOrder` instances that should be filled by the `BuyerPostedPriceAuction`.
  * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
  */
class BuyerPostedPriceAuction[A <: AskOrder with LimitPrice with Quantity, BB <: orderbooks.BidOrderBook[B], B <: BidOrder with LimitPrice with Persistent with Quantity]
                             (orderBook: BB, matchingRule: (A, BB) => Option[B], pricingRule: (A, B) => Price)
  extends PostedPriceAuction[A, BB, B](orderBook, matchingRule, pricingRule)


object BuyerPostedPriceAuction {

  /** Create an instance of a `BuyerPostedPriceAuction`.
    *
    * @param orderBook
    * @param matchingRule
    * @param pricingRule
    * @tparam A
    * @tparam BB
    * @tparam B
    * @return an instance of a `BuyerPostedPriceAuction`.
    */
  def apply[A <: AskOrder with LimitPrice with Quantity, BB <: orderbooks.BidOrderBook[B], B <: BidOrder with LimitPrice with Persistent with Quantity]
           (orderBook: BB, matchingRule: (A, BB) => Option[B], pricingRule: (A, B) => Price)
           : BuyerPostedPriceAuction[A, BB, B] = {
    new BuyerPostedPriceAuction(orderBook, matchingRule, pricingRule)
  }

}
