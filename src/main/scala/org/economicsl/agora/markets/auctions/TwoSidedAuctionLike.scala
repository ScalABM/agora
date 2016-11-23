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

import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** Trait defining a partial interface for a two-sided, posted price auction.
  *
  * @tparam A a sub-type of `AskOrder with Persistent`.
  * @tparam B a sub-type of `BidOrder with Persistent`.
  */
trait TwoSidedAuctionLike[A <: AskOrder with Persistent, B <: BidOrder with Persistent] {

  def cancel(order: A): Option[A]

  def cancel(order: B): Option[B]

  def place(order: A): Unit

  def place(order: B): Unit

  def tradable: Tradable

}
