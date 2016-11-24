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
package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


abstract class ContinuousDoubleAuction[A <: AskOrder, B <: BidOrder]
                                      (@volatile protected var askOrderBook: OrderBook[A with Persistent],
                                       askOrderMatchingRule: MatchingRule[A, B with Persistent],
                                       askOrderPricingRule: DiscriminatoryPricingRule[A, B with Persistent],
                                       @volatile protected var bidOrderBook: OrderBook[B with Persistent],
                                       bidOrderMatchingRule: MatchingRule[B, A with Persistent],
                                       bidOrderPricingRule: DiscriminatoryPricingRule[B, A with Persistent])
  extends DoubleAuction[A with Persistent, B with Persistent](askOrderBook, bidOrderBook) {

  /** Fill an incoming `AskOrder`.
    *
    * @param order an `AskOrder` instance.
    * @return
    */
  def fill(order: A): Option[Fill]

  /** Fill an incoming `BidOrder`.
    *
    * @param order a `BidOrder` instance.
    * @return
    */
  def fill(order: B): Option[Fill]

}
