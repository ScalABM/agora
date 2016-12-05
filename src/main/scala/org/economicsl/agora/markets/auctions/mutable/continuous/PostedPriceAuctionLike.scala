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
package org.economicsl.agora.markets.auctions.mutable.continuous

import org.economicsl.agora.markets.auctions.mutable.orderbooks
import org.economicsl.agora.markets.tradables.{LimitPrice, Quantity}
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import org.apache.commons.math3.stat


/** Trait defining a `PostedPriceAuction`.
  *
  * @tparam O the type of `Order` instances that should be filled by the `PostedPriceAuction`.
  * @tparam OB the type of `OrderBook` used to store the potential matches.
  */
trait PostedPriceAuctionLike[O <: Order with LimitPrice with Persistent with Quantity, OB <: orderbooks.OrderBook[O]] {

  final def cancel(order: O): Option[O] = orderBook.remove(order.issuer)

  final def clear(): Unit = orderBook.clear()

  final def place(order: O): Unit = orderBook.add(order)

  /** Summary of auction performance.
    *
    * @return an immutable summary of the underlying performance data.
    * @note not convinced that this method should be public (i.e., not sure it should be accessible to model agents).
    */
  final def performanceSummary: stat.descriptive.StatisticalSummary = performance.getSummary

  /** Storage container for data on auction performance
    *
    * @note auction performance is measured using trade surplus.
    */
  protected val performance: stat.descriptive.SummaryStatistics = new stat.descriptive.SummaryStatistics()

  protected def orderBook: OB

}