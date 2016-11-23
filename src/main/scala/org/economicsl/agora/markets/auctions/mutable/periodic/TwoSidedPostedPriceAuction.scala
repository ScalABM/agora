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
package org.economicsl.agora.markets.auctions.mutable.periodic


import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.orderbooks.OrderBookLike
import org.economicsl.agora.markets.auctions.TwoSidedAuctionLike
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.auctions.mutable.orderbooks.ExistingOrders
import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.Persistent


/** Trait defining the interface for a two-sided posted price auction.
  *
  * @tparam A a sub-type of `AskOrder with Persistent`.
  * @tparam B a sub-type of `BidOrder with Persistent`.
  */
trait TwoSidedPostedPriceAuction[A <: AskOrder with Persistent, B <: BidOrder with Persistent]
  extends TwoSidedAuctionLike[A, B] {

  /** Fill the existing orders.
    *
    * @return
    */
  def fill(): Option[Iterable[Fill]]

  /** Cancel an existing `AskOrder` and remove it from the `AskOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: A): Option[A] = askOrderBook.remove(order.uuid)

  /** Cancel an existing `BidOrder` and remove it from the `BidOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: B): Option[B] = bidOrderBook.remove(order.uuid)

  /** Remove all `AskOrder` and `BidOrder` instances from their respective `OrderBook`. */
  final def clear(): Unit = { askOrderBook.clear(); bidOrderBook.clear() }

  /** Place an `AskOrder` by adding it to the `AskOrderBook`.
    *
    * @param order
    */
  final def place(order: A): Unit = askOrderBook.add(order)

  /** Place a `BidOrder` by adding it to the `BidOrderBook`.
    *
    * @param order
    */
  final def place(order: B): Unit = bidOrderBook.add(order)

  protected def askOrderBook: OrderBookLike[A] with ExistingOrders[A]

  protected def bidOrderBook: OrderBookLike[B] with ExistingOrders[B]

}
