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

import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** In order for this to work we need immutable OrderBook! */
abstract class DoubleAuction[A <: AskOrder with Persistent, B <: BidOrder with Persistent]
                            (@volatile protected var askOrderBook: OrderBook[A],
                             @volatile protected var bidOrderBook: OrderBook[B]) {

  /** Cancel an existing `AskOrder` and remove it from the `AskOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: A): Unit = {
    askOrderBook = askOrderBook - order
  }

  /** Cancel an existing `BidOrder` and remove it from the `BidOrderBook`.
    *
    * @param order
    * @return
    */
  final def cancel(order: B): Unit = {
    bidOrderBook = bidOrderBook - order
  }

  final def clear(): Unit = {
    askOrderBook = askOrderBook.clear(); bidOrderBook = bidOrderBook.clear()
  }

  /** Add an `AskOrder` to the `AskOrderBook`.
    *
    * @param order
    */
  final def place(order: A): Unit = {
    askOrderBook = askOrderBook + order
  }

  /** Adds a `BidOrder` to the `BidOrderBook`.
    *
    * @param order
    */
  final def place(order: B): Unit = {
    bidOrderBook = bidOrderBook + order
  }

}
