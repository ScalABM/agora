/*
Copyright 2016 David R. Pugh

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
package markets.engines.orderbooks.mutable

import markets.engines.orderbooks.OrderBooks
import markets.orders.{AskOrder, BidOrder}

import scala.collection.mutable


/** Base trait describing a pair of generic mutable order books.
  *
  * @tparam CC1 some `MutableAskOrderBook` class used to store `AskOrders`.
  * @tparam CC2 some `MutableBidOrderBook` class used to store `BidOrders`.
  */
trait MutableOrderBooks[+CC1 <: mutable.Iterable[AskOrder], +CC2 <: mutable.Iterable[BidOrder]]
  extends OrderBooks[CC1, CC2]
