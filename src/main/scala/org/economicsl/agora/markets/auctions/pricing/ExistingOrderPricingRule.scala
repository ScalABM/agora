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
package org.economicsl.agora.markets.auctions.pricing

import org.economicsl.agora.markets.tradables.LimitPrice
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


/** Class defining a pricing rule where the `Fill` price is determined by the limit price of the existing order.
  *
  * The `ExistingOrderPricingRule` is a `WeightedAveragePricingRule` where the weight placed on the limit `Price`
  * of the incoming `Order with LimitPrice` is zero. The `Fill` `Price` is entirely determined by the limit `Price` of
  * the existing `Order with LimitPrice with Persistent`. The `ExistingOrderPricingRule` is the natural complement to
  * the `IncomingOrderPricingRule`.
  *
  * @tparam I the type of the incoming `Order with LimitPrice`.
  * @tparam E the type of the existing `Order with LimitPrice with Persistent`.
  * @note The `ExistingOrderPricingRule` is only weakly individually rational for the issuer of the `existingOrder`.
  *       Using this rule, the issuer if the `existingOrder` can not impact the `Fill` price and therefore can do no
  *       worse than truthfully revealing its reservation value. Wurman et al (2001) refer to this pricing rule as the
  *       "earlier bid" pricing rule.
  */
class ExistingOrderPricingRule[-I <: Order with LimitPrice, -E <: Order with LimitPrice with Persistent]
  extends WeightedAveragePricingRule[I, E](0.0)


object ExistingOrderPricingRule {

  def apply[I <: Order with LimitPrice, E <: Order with LimitPrice with Persistent](): ExistingOrderPricingRule[I, E] = {
    new ExistingOrderPricingRule[I, E]()
  }

}