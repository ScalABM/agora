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
package org.economicsl.agora.twosided.pricing

import org.economicsl.agora.onesided
import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder


/** Trait defining the interface for a two-sided `PricingFunction`.
  *
  * @tparam A
  * @tparam B
  */
trait PricingMechanism[A <: AskOrder, B <: BidOrder] {

  final def apply(bidOrder: B, askOrder: A): Long = {
    askOrderPricingFunction(bidOrder, askOrder)
  }

  final def apply(askOrder: A, bidOrder: B): Long = {
    bidOrderPricingFunction(askOrder, bidOrder)
  }

  /** One-side pricing function used to price an `AskOrder` that has been matched with a `BidOrder`. */
  def askOrderPricingFunction: onesided.pricing.PricingFunction[B, A]

  /** One-side matching function used to price a `BidOrder` that has been matched with an `AskOrder`. */
  def bidOrderPricingFunction: onesided.pricing.PricingFunction[A, B]

}
