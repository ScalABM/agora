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
import org.economicsl.agora.markets.tradables.Price
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** Trait defining the interface for a `SellerPostedPriceAuction`.
  *
  * @tparam B the type of `BidOrder` instances that should be filled by the `SellerPostedPriceAuction`.
  * @tparam A the type of `AskOrder` instances that are stored in the `AskOrderBook`.
  */
class SellerPostedPriceAuction[B <: BidOrder, AB <: orderbooks.AskOrderBook[A], A <: AskOrder]
                              (orderBook: AB, matchingRule: (B, AB) => Option[A], pricingRule: (B, A) => Price)
  extends PostedPriceAuction[B, AB, A](orderBook, matchingRule, pricingRule)


object SellerPostedPriceAuction {

  /** Create an instance of a `SellerPostedPriceAuction`.
    *
    * @param orderBook
    * @param matchingRule
    * @param pricingRule
    * @tparam B
    * @tparam AB
    * @tparam A
    * @return an instance of a `SellerPostedPriceAuction`.
    */
  def apply[B <: BidOrder, AB <: orderbooks.AskOrderBook[A], A <: AskOrder]
           (orderBook: AB, matchingRule: (B, AB) => Option[A], pricingRule: (B, A) => Price)
           : SellerPostedPriceAuction[B, AB, A] = {
    new SellerPostedPriceAuction[B, AB, A](orderBook, matchingRule, pricingRule)
  }

}
