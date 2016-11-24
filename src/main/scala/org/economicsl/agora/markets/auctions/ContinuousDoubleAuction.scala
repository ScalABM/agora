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
import org.economicsl.agora.markets.auctions.orderbooks.OrderBook
import org.economicsl.agora.markets.auctions.pricing.DiscriminatoryPricingRule
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


abstract class ContinuousDoubleAuction[A <: AskOrder, AB <: OrderBook[A with Persistent, AB],
                                       B <: BidOrder, BB <: OrderBook[B with Persistent, BB]]
                                      (askOrderBook: AB,
                                       val askOrderMatchingRule: (A, BB) => Option[(A, B with Persistent)],
                                       val askOrderPricingRule: DiscriminatoryPricingRule[A, B with Persistent],
                                       bidOrderBook: BB,
                                       val bidOrderMatchingRule: (B, AB) => Option[(B, A with Persistent)],
                                       val bidOrderPricingRule: DiscriminatoryPricingRule[B, A with Persistent])
  extends DoubleAuction[A with Persistent, AB, B with Persistent, BB](askOrderBook, bidOrderBook) {

  /** Fill an incoming `AskOrder`.
    *
    * @param order an `AskOrder` instance.
    * @return `None` if the incoming `AskOrder` could not be matched with an existing `BidOrder with Persistent`;
    *        otherwise `Some(fill)`.
    * @note a `Some(fill)` will be passed to a `SettlementMechanism` for further processing.
    */
  def fill(order: A): Option[Fill]

  /** Fill an incoming `BidOrder`.
    *
    * @param order a `BidOrder` instance.
    * @return `None` if the incoming `BidOrder` could not be matched with an existing `AskOrder with Persistent`;
    *        otherwise `Some(fill)`.
    * @note a `Some(fill)` will be passed to a `SettlementMechanism` for further processing.
    */
  def fill(order: B): Option[Fill]

}
