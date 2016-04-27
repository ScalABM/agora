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
package markets.engines.orderbooks

import markets.orders.{AskOrder, BidOrder}


/** Base trait describing a pair of generic order books.
  *
  * @tparam CC1 some `GenericAskOrderBook` class used to store `AskOrders`.
  * @tparam CC2 some `GenericBidOrderBook` class used to store `BidOrders`.
  */
trait GenericOrderBooks[+CC1 <: Iterable[AskOrder], +CC2 <: Iterable[BidOrder]] {

  def askOrderBook: GenericAskOrderBook[CC1]

  def bidOrderBook: GenericBidOrderBook[CC2]

}
