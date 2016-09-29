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
package markets.generic

import java.util.UUID

import markets.orders.{AskOrder, BidOrder}


/** Trait defining the interface for a `DoubleAuctionMechanism`.
  *
  * @tparam O1 the type of `AskOrder` accepted by the `AuctionMechanism`.
  * @tparam O2 the type of `BidOrder` accepted by the `AuctionMechanism`.
  * @tparam OB1 the type of `OrderBook` used to store the `AskOrder` instances.
  * @tparam OB2 the type of `OrderBook` used to store the `BidOrder` instances.
  */
trait DoubleAuctionMechanism[O1 <: AskOrder,
                             O2 <: BidOrder,
                             OB1 <: OrderBook[O1, collection.GenMap[UUID, O1]],
                             OB2 <: OrderBook[O2, collection.GenMap[UUID, O2]]]
  extends AuctionMechanism[O1, O2, OB1]{

  def cancel(order: O2): Option[O2] = oppositeOrderBook.remove(order.uuid)

  def fill(order: O1): Option[Fill]

  def place(order: O2): Unit = oppositeOrderBook.add(order)

  protected def oppositeOrderBook: OB2

}
