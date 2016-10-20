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
package markets.twosided.matching

import markets.onesided
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder
import markets.tradables.orders.{NonPriceCriteria, PriceCriteria}


class BestPriceMatchingFunction[A <: AskOrder with PriceCriteria[B] with NonPriceCriteria[B],
                                B <: BidOrder with PriceCriteria[A] with NonPriceCriteria[A]]
  extends MatchingFunction[A, B] {

  /** One-side matching function used to match an `AskOrder` with an order book containing `BidOrder` instances. */
  val askOrderMatchingFunction = new onesided.matching.BestPriceMatchingFunction[B, A]()

  /** One-side matching function used to match a `BidOrder` with an order book containing `AskOrder` instances. */
  val bidOrderMatchingFunction = new onesided.matching.BestPriceMatchingFunction[A, B]()

}


object BestPriceMatchingFunction {

  def apply[A <: AskOrder with PriceCriteria[B] with NonPriceCriteria[B],
            B <: BidOrder with PriceCriteria[A] with NonPriceCriteria[A]](): BestPriceMatchingFunction[A, B] = {
    new BestPriceMatchingFunction[A, B]()
  }

}