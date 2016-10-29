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
package org.economicsl.agora.twosided.auctions.continuous

import java.util.UUID

import org.economicsl.agora.tradables.orders.ask.AskOrder
import org.economicsl.agora.tradables.orders.bid.BidOrder
import org.economicsl.agora.{orderbooks, twosided, Fill}


/** Trait defining the interface for a `ContinuousDoubleAuction`. */
trait PostedPriceAuction[A <: AskOrder, AB <: orderbooks.OrderBook[A, collection.GenMap[UUID, A]],
                         B <: BidOrder, BB <: orderbooks.OrderBook[B, collection.GenMap[UUID, B]]]
  extends twosided.auctions.PostedPriceAuction[A, AB, B, BB] {

  def fill(order: A): Option[Fill]

  def fill(order: B): Option[Fill]

}