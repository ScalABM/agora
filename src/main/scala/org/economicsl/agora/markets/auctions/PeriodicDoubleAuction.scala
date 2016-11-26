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
import org.economicsl.agora.markets.auctions.pricing.UniformPricingRule
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


abstract class PeriodicDoubleAuction[A <: AskOrder with Persistent, AB <: OrderBook[A],
                                     B <: BidOrder with Persistent, BB <: OrderBook[B]]
                                    (askOrderBook: AB,
                                     bidOrderBook: BB,
                                     matchingRule: (AB, BB) => Option[Iterable[(A, B)]],
                                     pricingRule: UniformPricingRule[A, AB, B, BB])
  extends DoubleAuction[A, B](askOrderBook, bidOrderBook) {

  def fill(): Option[Iterable[Fill]]

}
