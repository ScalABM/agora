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

import markets.tradables.orders.ask.LimitAskOrder
import markets.tradables.orders.bid.LimitBidOrder


case class AveragePricingFunction(weight: Double) extends PricingFunction[LimitAskOrder, LimitBidOrder] {

  require(0 <= weight && weight <= 1, "Price must be individually rational!")

  def apply(askOrder: LimitAskOrder, bidOrder: LimitBidOrder): Long = {
    AveragePricingFunction.averagePrice(askOrder, bidOrder, weight)
  }

  def apply(bidOrder: LimitBidOrder, askOrder: LimitAskOrder): Long = {
    AveragePricingFunction.averagePrice(askOrder, bidOrder, weight)
  }

}


object AveragePricingFunction {

  def averagePrice(askOrder: LimitAskOrder, bidOrder: LimitBidOrder, weight: Double): Long = {
    (weight * askOrder.price + (1 - weight) * bidOrder.price).toLong  // hack probably should be using Double!
  }

}