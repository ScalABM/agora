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
package markets.tradables.orders.ask

import java.util.UUID

import markets.tradables.{LimitPrice, Tradable}
import markets.tradables.orders.Predicate
import markets.tradables.orders.bid.{BidOrder, LimitBidOrder, MarketBidOrder}


/** Trait defining an order to sell some `Tradable` at a price greater than or equal to some limit price. */
trait LimitAskOrder extends AskOrder with LimitPrice with Predicate[BidOrder] {

  /** Boolean function used to determine whether some `BidOrder` is an acceptable match for a `LimitAskOrder`
    *
    * @return a boolean function that returns `true` if the `BidOrder` is acceptable and `false` otherwise.
    */
  def isAcceptable: (BidOrder) => Boolean = {
    case order: MarketBidOrder => order.tradable == this.tradable
    case order: LimitBidOrder => (order.tradable == this.tradable) && (this.limit <= order.limit)
    case _ => false
  }

}

/** Companion object for the `LimitAskOrder` trait.
  *
  * The companion object provides various orderings for `LimitAskOrder` instances.
  */
object LimitAskOrder {

  /** By default, instances of `LimitAskOrder` are ordered based on `limit` price from lowest to highest */
  implicit def ordering[O <: LimitAskOrder]: Ordering[O] = LimitPrice.ordering

  /** The highest priority `LimitAskOrder` is the one with the lowest `limit` price. */
  def priority[O <: LimitAskOrder]: Ordering[O] = LimitPrice.ordering.reverse

  /** Creates an instance of a `LimitAskOrder`.
    *
    * @param issuer
    * @param limit
    * @param quantity
    * @param timestamp
    * @param tradable
    * @param uuid
    * @return an instance of a `LimitAskOrder`.
    */
  def apply(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): LimitAskOrder = {
    DefaultLimitAskOrder(issuer, limit, quantity, timestamp, tradable, uuid)
  }

  private case class DefaultLimitAskOrder(issuer: UUID, limit: Long, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID)
    extends LimitAskOrder

}