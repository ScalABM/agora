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

import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.Price
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


/** Mixin trait defining whether or not best ask and bid orders are publicly available.
  *
  * @tparam A
  * @tparam B
  */
sealed trait OrderBookAvailability[A <: LimitAskOrder with Persistent, AB <: AskOrderBook[A],
                                   B <: LimitBidOrder with Persistent, BB <: BidOrderBook[B]] {
  this: TwoSidedAuctionLike[A, AB, B, BB] =>

}


trait SealedOrderBooks[A <: LimitAskOrder with Persistent, AB <: AskOrderBook[A],
                       B <: LimitBidOrder with Persistent, BB <: BidOrderBook[B]]
  extends OrderBookAvailability[A, AB, B, BB] {
  this: TwoSidedAuctionLike[A, AB, B, BB] =>
}


trait OpenOrderBooks[A <: LimitAskOrder with Persistent, AB <: AskOrderBook[A],
                     B <: LimitBidOrder with Persistent, BB <: BidOrderBook[B]]
  extends OrderBookAvailability[A, AB, B, BB] {
  this: TwoSidedAuctionLike[A, AB, B, BB] =>

  def bestLimitAskOrder(): Option[A] = {
    askOrderBook.find(askOrder => askOrder.limit > Price.MinValue)
  }

  def bestLimitBidOrder(): Option[B] = {
    bidOrderBook.find(bidOrder => bidOrder.limit < Price.MinValue)
  }

}