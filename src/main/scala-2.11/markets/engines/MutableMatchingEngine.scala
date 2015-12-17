/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.clearing.engines

import markets.orders.{BidOrder, AskOrder}
import markets.orders.limit.LimitOrderLike

import scala.collection.mutable


/** Base trait for all matching engines that have an internal mutable orderbooks. */
trait MutableMatchingEngine extends MatchingEngine {

  /* Mutable collection of ask orders for internal use only! */
  protected val _askOrderBook: mutable.Iterable[AskOrder]

  /* Mutable collection of bid orders for internal use only! */
  protected val _bidOrderBook: mutable.Iterable[BidOrder]

  def bestLimitAskOrder: Option[AskOrder] = {
    _askOrderBook.find(askOrder => askOrder.isInstanceOf[LimitOrderLike])
  }

  def bestLimitBidOrder: Option[BidOrder] = {
    _bidOrderBook.find(order => order.isInstanceOf[LimitOrderLike])
  }

}