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
package markets.matching.twosided

import markets.matching.onesided
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder
import markets.tradables.orders.Predicate


/** Class defining a two-sided `MatchingFunction` that matches an `AskOrder` (`BidOrder`) with the first acceptable `BidOrder`
  * (`AskOrder`) found in an `OrderBook`.
  *
  * @tparam A
  * @tparam B
  */
class FindFirstMatchingFunction[A <: AskOrder with Predicate[B], B <: BidOrder with Predicate[A]]
  extends MatchingFunction[A, B] {

  /** One-side matching function used to match an `AskOrder` with an order book containing `BidOrder` instances. */
  protected val askOrderMatchingFunction = new onesided.FindFirstMatchingFunction[B, A]()

  /** One-side matching function used to match a `BidOrder` with an order book containing `AskOrder` instances. */
  protected val bidOrderMatchingFunction = new onesided.FindFirstMatchingFunction[A, B]()

}