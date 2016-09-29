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

import markets.orders.{Order, Price, Quantity}


/** Trait defining the interface for an `Auction`. */
trait AuctionMechanism[O1 <: Order with Price with Quantity,
                       O2 <: Order with Price with Quantity,
                       OB <: OrderBook[O1, collection.GenMap[UUID, O1]]] {

  def cancel(order: O1): Option[O1] = orderBook.remove(order.uuid)

  def fill(order: O2): Option[Fill]

  def place(order: O1): Unit = orderBook.add(order)

  protected def matchingMechanism: MatchingMechanism[O1, O2, OB]

  protected def pricingMechanism: PricingMechanism[O1, O2, OB]

  protected def orderBook: OB

}
