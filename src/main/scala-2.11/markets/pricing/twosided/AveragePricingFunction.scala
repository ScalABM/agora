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
package markets.pricing.twosided

import markets.pricing.onesided
import markets.tradables.LimitPrice
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


class AveragePricingFunction[A <: AskOrder with LimitPrice, B <: BidOrder with LimitPrice](weight: Double)
  extends PricingFunction[A, B] {

  /** One-side pricing function used to price an `AskOrder` that has been matched with a `BidOrder`. */
  protected val askOrderPricingFunction = onesided.AveragePricingFunction[B, A](weight)

  /** One-side pricing function used to price a `BidOrder` that has been matched with an `AskOrder`. */
  protected val bidOrderPricingFunction = onesided.AveragePricingFunction[A, B](weight)

}
