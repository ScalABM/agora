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

import java.util.UUID

import markets.onesided
import markets.orderbooks
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


/** Trait defining the interface for a two-sided `MatchingFunction`. */
trait MatchingFunction[A <: AskOrder, B <: BidOrder] {

  final def apply(order: A, orderBook: orderbooks.OrderBook[B, collection.GenMap[UUID, B]]): Option[(A, B)] = {
    askOrderMatchingFunction(order, orderBook)
  }

  final def apply(order: B, orderBook: orderbooks.OrderBook[A, collection.GenMap[UUID, A]]): Option[(B, A)] = {
    bidOrderMatchingFunction(order, orderBook)
  }

  /** One-side matching function used to match an `AskOrder` with an order book containing `BidOrder` instances. */
  def askOrderMatchingFunction: onesided.matching.MatchingFunction[B, A]

  /** One-side matching function used to match a `BidOrder` with an order book containing `AskOrder` instances. */
  def bidOrderMatchingFunction: onesided.matching.MatchingFunction[A, B]

}
