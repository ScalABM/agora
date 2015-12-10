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

import markets.clearing.engines.matches.Matching
import markets.orders.{BidOrder, AskOrder, Order}

import scala.collection.immutable


/** Base trait for all matching engines.
  *
  * @note A `MatchingEngine` object should handle any necessary queuing of ask and bid orders,
  *       order execution (specifically price formation and quantity determination), and generate
  *       matches orders.
  */
trait MatchingEngine {

  /** MatchingEngine should maintain some collection of ask orders. */
  def askOrderBook: immutable.Iterable[AskOrder]

  /** MatchingEngine should maintain some collection of bid orders. */
  def bidOrderBook: immutable.Iterable[BidOrder]

  def bestLimitAskOrder: Option[AskOrder]

  def bestLimitBidOrder: Option[BidOrder]

  /** Find a match for the incoming order.
    *
    * @param incoming the order to be matched.
    * @return a collection of matches.
    * @note Depending on size of the incoming order and the state of the market when the order is
    *       received, a single incoming order may generate several matches.
    */
  def findMatch(incoming: Order): Option[immutable.Queue[Matching]]

  /** Removes an order from the matching engine.
    *
    * @param existing the order that is to be removed.
    * @return Some(order) if the order exists in the order book, else None.
    */
  def remove(existing: Order): Option[Order]

}
