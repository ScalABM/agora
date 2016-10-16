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


/** Class modeling a best limit pricing function.
  *
  * Best limit pricing functions are commonly used to price financial assets.
  *
  * @note The `BestLimitPricingFunction` is only weakly individually rational for the `existingOrder`.
  */
class BestLimitPricingFunction extends PricingFunction[LimitAskOrder, LimitBidOrder] {

  /** Returns the best limit price for an incoming `LimitAskOrder`.
    *
    * @param incomingOrder
    * @param existingOrder
    * @return
    * @note the best limit price from for an incoming `LimitAskOrder` will always be the price of the existing
    *       `LimitBidOrder` with which it has been matched.
    */
  def apply(incomingOrder: LimitAskOrder, existingOrder: LimitBidOrder): Long = existingOrder.limit

  /** Returns the best limit price from the perspective of an incoming `LimitBidOrder`.
    *
    * @param incomingOrder
    * @param existingOrder
    * @return
    * @note the best limit price from for an incoming `LimitBidOrder` will always be the price of the existing
    *       `LimitAskOrder` with which it has been matched.
    */
  def apply(incomingOrder: LimitBidOrder, existingOrder: LimitAskOrder): Long = existingOrder.limit

}


object BestLimitPricingFunction {

  def apply(): BestLimitPricingFunction = new BestLimitPricingFunction()

}