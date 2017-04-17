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

import markets.clearing.strategies.PriceFormationStrategy
import markets.orders.filled.FilledOrderLike
import markets.orders.limit.LimitOrderLike
import markets.orders.{BidOrderLike, AskOrderLike, OrderLike}

import scala.collection.immutable


/** Base trait for all matching engines.
  *
  * @note A `MatchingEngineLike` object should handle any necessary queuing of ask and bid orders,
  *       order execution (specifically price formation and quantity determination), and generate
  *       filled orders.
  */
trait MatchingEngineLike {
  this: PriceFormationStrategy =>

  /** MatchingEngine should maintain some collection of orders. */
  def askOrderBook: immutable.Iterable[AskOrderLike]

  def bidOrderBook: immutable.Iterable[BidOrderLike]

  def bestLimitOrder(orderBook: immutable.Iterable[OrderLike]): Option[OrderLike] = {
    orderBook.find(order => order.isInstanceOf[LimitOrderLike])
  }

  def referencePrice: Long

  /** Fill an incoming order.
    *
    * @param order the order to be filled.
    * @return a collection of filled orders.
    * @note Depending on size of the incoming order and the state of the market when the order is
    *       received, a single incoming order may generate several filled orders.
    */
  def fillIncomingOrder(order: OrderLike): Option[immutable.Iterable[FilledOrderLike]]

}
