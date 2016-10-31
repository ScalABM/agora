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
package org.economicsl.agora.markets.auctions.periodic


import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.TwoSidedAuctionLike
import org.economicsl.agora.markets.auctions.orderbooks.OrderBookLike
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


trait TwoSidedPostedPriceAuction[A <: AskOrder, B <: BidOrder] extends TwoSidedAuctionLike[A, B] {

  def fill(): Iterable[Fill]

  protected def askOrderBook: OrderBookLike[A]

  protected def bidOrderBook: OrderBookLike[B]

}
