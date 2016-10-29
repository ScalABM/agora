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
package org.economicsl.agora.onesided.auctions

import java.util.UUID

import org.economicsl.agora.orderbooks
import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder

import scala.collection.GenMap


/** Trait defining the interface for a `SellerPostedPriceAuction`.
  *
  * @tparam B the type of `BidOrder` instances that should be filled by the `SellerPostedPriceAuction`.
  * @tparam AB the type of `OrderBook` used to store the posted `AskOrder` instances.
  * @tparam A the type of `AskOrder` instances that are stored in the `OrderBook`.
  */
trait SellerPostedPriceAuction[B <: BidOrder, AB <: orderbooks.OrderBook[A, GenMap[UUID, A]], A <: AskOrder]
  extends PostedPriceAuction[B, AB, A]
