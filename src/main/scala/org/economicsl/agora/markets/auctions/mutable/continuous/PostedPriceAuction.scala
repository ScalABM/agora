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

import java.util.UUID

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.RandomUUIDGenerator
import org.economicsl.agora.markets.auctions.mutable.orderbooks
import org.economicsl.agora.markets.tradables.{Price, Quantity}
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


/** Class defining a `PostedPriceAuction`.
  *
  * @param orderBook
  * @param matchingRule
  * @param pricingRule
  * @tparam O1 the type of `Order` instances that should be filled by the `PostedPriceAuction`.
  * @tparam OB the type of `OrderBook` used to store the potential matches.
  * @tparam O2 the type of `Order` instances that are potential matches and are stored in the `OrderBook`.
  */
class PostedPriceAuction[O1 <: Order with Quantity, OB <: orderbooks.OrderBook[O2], O2 <: Order with Persistent with Quantity]
                        (orderBook: OB, matchingRule: (O1, OB) => Option[O2], pricingRule: (O1, O2) => Price)
  extends RandomUUIDGenerator {

  final def cancel(order: O2): Option[O2] = orderBook.remove(order.uuid)

  final def fill(order: O1): Option[Fill] = {
    val matchingOrders = matchingRule(order, orderBook) // eventually this will return an iterable!
    matchingOrders.foreach(matchingOrder => orderBook.remove(matchingOrder.uuid)) // SIDE EFFECT!
    matchingOrders.map { matchingOrder =>
      val price = pricingRule(order, matchingOrder)
      val quantity = math.min(order.quantity, matchingOrder.quantity) // not dealing with residual orders!
      new Fill(order.issuer, matchingOrder.issuer, price, quantity, orderBook.tradable)
    }
  }

  final def place(order: O2): UUID = {
    val uuid = nextUUID(); orderBook.add(uuid -> order); uuid
  }

}


object PostedPriceAuction {

  def apply[O1 <: Order with Quantity, OB <: orderbooks.OrderBook[O2], O2 <: Order with Persistent with Quantity]
           (orderBook: OB, matchingRule: (O1, OB) => Option[O2], pricingRule: (O1, O2) => Price)
           : PostedPriceAuction[O1, OB, O2] = {
    new PostedPriceAuction(orderBook, matchingRule, pricingRule)
  }

}