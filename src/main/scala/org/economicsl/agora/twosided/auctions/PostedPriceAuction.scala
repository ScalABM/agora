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
package org.economicsl.agora.twosided.auctions

import java.util.UUID

import org.economicsl.agora.generics.orderbooks.OrderBookLike
import org.economicsl.agora.onesided
import org.economicsl.agora.orderbooks
import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder


/** Trait defining the interface for a Double Auction.
  *
  * @tparam A
  * @tparam B
  */
trait PostedPriceAuction[A <: AskOrder, AB <: OrderBookLike[A], B <: BidOrder, BB <: OrderBookLike[B]] {

  def cancel(order: A): Option[A] = sellerPostedPriceAuction.cancel(order)

  def cancel(order: B): Option[B] = buyerPostedPriceAuction.cancel(order)

  def place(order: A): Unit = sellerPostedPriceAuction.place(order)

  def place(order: B): Unit = buyerPostedPriceAuction.place(order)

  protected def buyerPostedPriceAuction: onesided.auctions.BuyerPostedPriceAuction[A, BB, B]

  protected def sellerPostedPriceAuction: onesided.auctions.SellerPostedPriceAuction[B, AB, A]

}