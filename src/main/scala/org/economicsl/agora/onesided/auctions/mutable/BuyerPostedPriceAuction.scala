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
import org.economicsl.agora.orderbooks.mutable.bidorderbooks.{BidOrderBook, HashBidOrderBook, SortedBidOrderBook, SortedHashBidOrderBook}
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder
import org.economicsl.agora.{Fill, generics}


/** Trait defining the interface for a `BuyerPostedPriceAuction`.
  *
  * @tparam A the type of `AskOrder` instances that are filled by the `BuyerPostedPriceAuction`.
  * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
  */
trait BuyerPostedPriceAuction[A <: AskOrder, BB <: BidOrderBook[B],  B <: BidOrder]
  extends generics.auctions.mutable.onesided.PostedPriceAuction[A, BB, B] {

  /** Cancel an existing `BidOrder` and remove it from the `BidOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: B): Option[B] = orderBook.remove(order.uuid)

  /** Place a `BidOrder` into the `BidOrderBook`.
    *
    * @param order
    */
  final def place(order: B): Unit = orderBook.add(order)

  /** Find a matching `BidOrder` in a `BidOrderBook`, for a given `AskOrder`.
    *
    * @param askOrder
    * @param bidOrderBook
    * @return
    */
  def findMatchFor(askOrder: A, bidOrderBook: BB): Option[B]

  /** Determines a price given an `AskOrder` and a matching `BidOrder`.
    *
    * @param askOrder
    * @param bidOrder
    * @return
    */
  def findPriceFor(askOrder: A, bidOrder: B): Long

}


/** Companion object for the `BuyerPostedPriceAuction`.
  *
  * Provides constructors for default implementations.
  */
object BuyerPostedPriceAuction {

  /** Create an instance of a `BuyerPostedPriceAuction`.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @tparam A the type of `AskOrder` instances that should be filled by the `BuyerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
    * @return
    */
  def apply[A <: AskOrder, B <: BidOrder](matchingFunction: MatchingFunction[A, BidOrderBook[B], B],
                                          pricingFunction: PricingFunction[A, B],
                                          tradable: Tradable): BuyerPostedPriceAuction[A, BidOrderBook[B], B] = {
    new DefaultImpl[A, B](matchingFunction, pricingFunction, tradable)
  }

  /** Create an instance of a `BuyerPostedPriceAuction`.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @param ordering an `Ordering` defined over `BidOrder` instances.
    * @tparam A the type of `AskOrder` instances filled by the `BuyerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances stored in the underlying `SortedBidOrderBook`.
    * @return
    */
  def apply[A <: AskOrder, B <: BidOrder](matchingFunction: MatchingFunction[A, SortedBidOrderBook[B], B],
                                          pricingFunction: PricingFunction[A, B],
                                          tradable: Tradable)
                                         (implicit ordering: Ordering[B]): BuyerPostedPriceAuction[A, SortedBidOrderBook[B], B] = {
    new DefaultSortedImpl[A, B](matchingFunction, pricingFunction, tradable)
  }

  /** Default implementation of a `BuyerPostedPriceAuction`.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @tparam A the type of `AskOrder` instances that should be filled by the `BuyerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances that are stored in the `OrderBook`.
    */
  private[this] class DefaultImpl[A <: AskOrder, B <: BidOrder](matchingFunction: MatchingFunction[A, BidOrderBook[B], B],
                                                                pricingFunction: PricingFunction[A, B],
                                                                tradable: Tradable)
    extends BuyerPostedPriceAuction[A, BidOrderBook[B], B] {

    def fill(order: A): Option[Fill] = {
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

    def findMatchFor(askOrder: A, bidOrderBook: BidOrderBook[B]): Option[B] = {
      matchingFunction(askOrder, bidOrderBook)
    }

    def findPriceFor(askOrder: A, bidOrder: B) = {
      pricingFunction(askOrder, bidOrder)
    }

    protected val orderBook = new HashBidOrderBook[B](tradable)

  }


  /** Default implementation of a `BuyerPostedPriceAuction` where the underlying `OrderBook` is sorted.
    *
    * @param matchingFunction
    * @param pricingFunction
    * @param tradable
    * @param ordering an `Ordering` defined over `BidOrder` instances.
    * @tparam A the type of `AskOrder` instances filled by the `BuyerPostedPriceAuction`.
    * @tparam B the type of `BidOrder` instances stored in the underlying `SortedBidOrderBook`.
    */
  private[this] class DefaultSortedImpl[A <: AskOrder, B <: BidOrder](matchingFunction: MatchingFunction[A, SortedBidOrderBook[B], B],
                                                                      pricingFunction: PricingFunction[A, B],
                                                                      tradable: Tradable)
                                                                     (implicit ordering: Ordering[B])
    extends BuyerPostedPriceAuction[A, SortedBidOrderBook[B], B] {

    def fill(order: A): Option[Fill] = {
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

    def findMatchFor(askOrder: A, bidOrderBook: SortedBidOrderBook[B]): Option[B] = {
      matchingFunction(askOrder, bidOrderBook)
    }

    def findPriceFor(askOrder: A, bidOrder: B) = {
      pricingFunction(askOrder, bidOrder)
    }

    protected val orderBook = new SortedHashBidOrderBook[B](tradable)(ordering)

  }

}