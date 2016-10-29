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
package org.economicsl.agora.onesided.pricing

import org.economicsl.agora.tradables.LimitPrice
import org.economicsl.agora.tradables.orders.Order


class AveragePricingFunction[-O1 <: Order with LimitPrice, -O2 <: Order with LimitPrice](val weight: Double)
  extends PricingFunction[O1, O2] {

  require(0 <= weight && weight <= 1, "Price must be individually rational!")

  def apply(order1: O1, order2: O2): Long = {
    AveragePricingFunction.averagePrice(order1, order2, weight)
  }

}


object AveragePricingFunction {

  def apply[O1 <: Order with LimitPrice, O2 <: Order with LimitPrice](weight: Double): AveragePricingFunction[O1, O2] = {
    new AveragePricingFunction[O1, O2](weight)
  }

  def averagePrice[O1 <: Order with LimitPrice, O2 <: Order with LimitPrice](order1: O1, order2: O2, weight: Double): Long = {
    (weight * order1.limit + (1 - weight) * order2.limit).toLong  // hack probably should be using Double!
  }

}