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
package markets.pricing

import markets.tradables.Price
import markets.tradables.orders.Order


case class AveragePricingFunction[O1 <: Order with Price, O2 <: Order with Price](weight: Double)
  extends PricingFunction[O1, O2] {

  require(0 <= weight && weight <= 1, "Price must be individually rational!")

  def apply(order1: O1, order2: O2): Long = {
    AveragePricingFunction.averagePrice(order1, order2, weight)
  }

}


object AveragePricingFunction {

  def averagePrice(askOrder: LimitAskOrder, bidOrder: LimitBidOrder, weight: Double): Long = {
    (weight * askOrder.limit + (1 - weight) * bidOrder.limit).toLong  // hack probably should be using Double!
  }

}