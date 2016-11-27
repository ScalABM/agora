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

import org.economicsl.agora.markets.tradables.{LimitPrice, Price}
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


/** Class defining a discriminatory pricing rule that computes the price as a weighted average.
  *
  * @param weight the weight placed on the limit `Price` of the incoming `Order` when computing the `Price`.
  * @tparam I the incoming `Order` type.
  * @tparam E the existing `Order with Persistent` type.
  */
class WeightedAveragePricingRule[-I <: Order with LimitPrice, -E <: Order with LimitPrice with Persistent](weight: Double)
  extends DiscriminatoryPricingRule[I, E] {

  require(0 <= weight && weight <= 1, "Price must be individually rational!")

  def apply(incoming: I, existing: E): Price = {
    Price(weight * incoming.limit.value + (1 - weight) * existing.limit.value)
  }

}


/** Companion object for the `WeightedAveragePricingRule` class.
  *
  * Provides auxiliary constructor.
  */
object WeightedAveragePricingRule {

  def apply[I <: Order with LimitPrice, E <: Order with LimitPrice with Persistent]
           (weight: Double): WeightedAveragePricingRule[I, E] = {
    new WeightedAveragePricingRule[I, E](weight)
  }

}