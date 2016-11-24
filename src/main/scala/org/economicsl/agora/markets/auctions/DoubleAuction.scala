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
package org.economicsl.agora.markets.auctions

import java.util.UUID

import org.economicsl.agora.markets.auctions.orderbooks.OrderBook
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** In order for this to work we need immutable OrderBook! */
abstract class DoubleAuction[A <: AskOrder with Persistent, AB <: OrderBook[A, AB],
                             B <: BidOrder with Persistent, BB <: OrderBook[B, BB]]
                            (@volatile private[this] var askOrderBook: AB, @volatile private[this] var bidOrderBook: BB) {

  /** Cancel an existing `AskOrder` and remove it from the `AskOrderBook`.
    *
    * @param uuid
    */
  final def cancel(uuid: UUID): Unit = {
    if (askOrderBook.contains(uuid)) askOrderBook = askOrderBook - uuid else bidOrderBook = bidOrderBook - uuid
  }

  final def clear(): Unit = {
    askOrderBook = askOrderBook.clear(); bidOrderBook = bidOrderBook.clear()
  }

  /** Add an `AskOrder` to the `AskOrderBook`.
    *
    * @param kv
    */
  final def place(kv: (UUID, A)): Unit = {
    askOrderBook = askOrderBook + kv
  }

  /** Adds a `BidOrder` to the `BidOrderBook`.
    *
    * @param kv
    */
  final def place(kv: (UUID, B)): Unit = {
    bidOrderBook = bidOrderBook + kv
  }

}
