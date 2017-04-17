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

import markets.orderbooks.{BidOrderBookLike, AskOrderBookLike}
import markets.orders.{AskOrderLike, BidOrderLike, FilledOrderLike, OrderLike}

import scala.collection.immutable
import scala.util.Try


class BrokenMatchingEngine extends MatchingEngineLike {

  val askOrderBook: Option[AskOrderBookLike] = None

  val bidOrderBook: Option[BidOrderBookLike] = None

  def crosses(ask: AskOrderLike, bid: BidOrderLike): Boolean = true

  /** A `BrokenMatchingEngine` always fails to fill orders. */
  def fillIncomingOrder(order: OrderLike): Try[immutable.Seq[FilledOrderLike]] = {
    throw new Exception()
  }

  def formPrice(ask: AskOrderLike, bid: BidOrderLike): Long = 1
  
}
