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
package markets.twosided.auctions

import markets.onesided.auctions
import markets.tradables.orders.Persistent
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder


/** Trait defining the interface for a Double Auction.
  *
  * @tparam A
  * @tparam B
  * @note a `DoubleAuction` is a composition of a `BuyerPostedPriceAuction` and a `SellerPostedPriceAuction`.
  */
trait PostedPriceAuction[A <: AskOrder, B <: BidOrder] {

  def cancel(order: A with Persistent): Option[A] = sellerPostedPriceAuction.cancel(order)

  def cancel(order: B with Persistent): Option[B] = buyerPostedPriceAuction.cancel(order)

  def place(order: A with Persistent): Unit = sellerPostedPriceAuction.place(order)

  def place(order: B with Persistent): Unit = buyerPostedPriceAuction.place(order)

  protected def buyerPostedPriceAuction: auctions.BuyerPostedPriceAuction[A, B with Persistent]

  protected def sellerPostedPriceAuction: auctions.SellerPostedPriceAuction[A with Persistent, B]

}