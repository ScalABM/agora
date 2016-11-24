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


/** Class defining a function that computes the price as a weighted average of the ask and bid limit prices. */
class WeightedAveragePricingRule[-O1 <: Order with LimitPrice, -O2 <: Order with LimitPrice with Persistent](weight: Double)
  extends DiscriminatoryPricingRule[O1, O2] {

  require(0 <= weight && weight <= 1, "Price must be individually rational!")

  def apply(order1: O1, order2: O2): Price = {
    WeightedAveragePricingRule.averagePrice(order1, order2, weight)
  }

}


object WeightedAveragePricingRule {

  def apply[O1 <: Order with LimitPrice, O2 <: Order with LimitPrice with Persistent](weight: Double): WeightedAveragePricingRule[O1, O2] = {
    new WeightedAveragePricingRule[O1, O2](weight)
  }

  def averagePrice[O1 <: Order with LimitPrice, O2 <: Order with LimitPrice](order1: O1, order2: O2, weight: Double): Price = {
    Price(weight * order1.limit.value + (1 - weight) * order2.limit.value)
  }

}