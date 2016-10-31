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
package org.economicsl.agora.onesided.auctions.mutable

import org.economicsl.agora.onesided.matching.MatchingFunction
import org.economicsl.agora.onesided.pricing.PricingFunction
import org.economicsl.agora.orderbooks.mutable.askorderbooks.{AskOrderBook, HashAskOrderBook, SortedAskOrderBook, SortedHashAskOrderBook}
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder
import org.economicsl.agora.{Fill, generics}


/** Trait defining the interface for a `SellerPostedPriceAuctionn`.
  *
  * @tparam B the type of `BidOrder` instances that are filled by the `SellerPostedPriceAuctionn`.
  * @tparam A the type of `AskOrder` instances that are stored in the `AskOrderBook`.
  */
trait SellerPostedPriceAuction[B <: BidOrder, AB <: AskOrderBook[A],  A <: AskOrder]
  extends generics.auctions.mutable.onesided.PostedPriceAuction[B, AB, A] {

  /** Cancel an existing `AskOrder` and remove it from the `AskOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: A): Option[A] = orderBook.remove(order.uuid)

  /** Place a `AskOrder` into the `AskOrderBook`.
    *
    * @param order
    */
  final def place(order: A): Unit = orderBook.add(order)

  /** Find a matching `AskOrder` in a `AskOrderBook`, for a given `BidOrder`.
    *
    * @param askOrder
    * @param bidOrderBook
    * @return
    */
  def findMatchFor(askOrder: B, bidOrderBook: AB): Option[A]

  /** Determines a price given an `BidOrder` and a matching `AskOrder`.
    *
    * @param bidOrder
    * @param askOrder
    * @return
    */
  def findPriceFor(bidOrder: B, askOrder: A): Long

}


/** Companion object for the `SellerPostedPriceAuction`.
  *
  * Provides constructors for default implementations.
  */
object SellerPostedPriceAuctionn {

  /** Create an instance of a `SellerPostedPriceAuction`.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @tparam A the type of `AskOrder` instances that should be filled by the `SellerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
    * @return
    */
  def apply[B <: BidOrder, A <: AskOrder](matchingFunction: MatchingFunction[B, AskOrderBook[A], A],
                                          pricingFunction: PricingFunction[B, A],
                                          tradable: Tradable): SellerPostedPriceAuction[B, AskOrderBook[A], A] = {
    new DefaultImpl[B, A](matchingFunction, pricingFunction, tradable)
  }

  /** Create an instance of a `SellerPostedPriceAuctionn`.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @param ordering an `Ordering` defined over `BidOrder` instances.
    * @tparam A the type of `AskOrder` instances filled by the `SellerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances stored in the underlying `SortedBidOrderBook`.
    * @return
    */
  def apply[B <: BidOrder, A <: AskOrder](matchingFunction: MatchingFunction[B, SortedAskOrderBook[A], A],
                                          pricingFunction: PricingFunction[B, A],
                                          tradable: Tradable)
                                         (implicit ordering: Ordering[A]): SellerPostedPriceAuction[B, SortedAskOrderBook[A], A] = {
    new DefaultSortedImpl[B, A](matchingFunction, pricingFunction, tradable)
  }

  /** Default implementation of a `SellerPostedPriceAuction`.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @tparam A the type of `AskOrder` instances that should be filled by the `SellerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
    */
  private[this] class DefaultImpl[B <: BidOrder, A <: AskOrder](matchingFunction: MatchingFunction[B, AskOrderBook[A], A],
                                                                pricingFunction: PricingFunction[B, A],
                                                                tradable: Tradable)
    extends SellerPostedPriceAuction[B, AskOrderBook[A], A] {

    def fill(order: B): Option[Fill] = {
      val matchingBidOrder = findMatchFor(order, orderBook)
      matchingBidOrder match {
        case Some(bidOrder) =>
          orderBook.remove(bidOrder.uuid) // SIDE EFFECT!
        val price = findPriceFor(order, bidOrder)
          val quantity = math.min(order.quantity, bidOrder.quantity)
          Some(new Fill(bidOrder.issuer, order.issuer, price, quantity, tradable))
        case None => None
      }
    }

    def findMatchFor(bidOrder: B, askOrderBook: AskOrderBook[A]): Option[A] = {
      matchingFunction(bidOrder, askOrderBook)
    }

    def findPriceFor(bidOrder: B, askOrder: A): Long = {
      pricingFunction(bidOrder, askOrder)
    }

    protected val orderBook = new HashAskOrderBook[A](tradable)

  }


  /** Default implementation of a `SellerPostedPriceAuction` where the underlying `OrderBook` is sorted.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @param ordering an `Ordering` defined over `BidOrder` instances.
    * @tparam A the type of `AskOrder` instances filled by the `SellerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances stored in the underlying `SortedBidOrderBook`.
    */
  private[this] class DefaultSortedImpl[B <: BidOrder, A <: AskOrder](matchingFunction: MatchingFunction[B, SortedAskOrderBook[A], A],
                                                                      pricingFunction: PricingFunction[B, A],
                                                                      tradable: Tradable)
                                                                     (implicit ordering: Ordering[A])
    extends SellerPostedPriceAuction[B, SortedAskOrderBook[A], A] {

    def fill(order: B): Option[Fill] = {
      val matchingBidOrder = findMatchFor(order, orderBook)
      matchingBidOrder match {
        case Some(bidOrder) =>
          orderBook.remove(bidOrder.uuid) // SIDE EFFECT!
        val price = findPriceFor(order, bidOrder)
          val quantity = math.min(order.quantity, bidOrder.quantity)
          Some(new Fill(bidOrder.issuer, order.issuer, price, quantity, tradable))
        case None => None
      }
    }

    def findMatchFor(bidOrder: B, askOrderBook: SortedAskOrderBook[A]): Option[A] = {
      matchingFunction(bidOrder, askOrderBook)
    }

    def findPriceFor(bidOrder: B, askOrder: A) = {
      pricingFunction(bidOrder, askOrder)
    }

    protected val orderBook = new SortedHashAskOrderBook[A](tradable)(ordering)

  }

}