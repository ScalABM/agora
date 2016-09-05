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
package markets.engines

import markets.orderbooks.AbstractOrderBook
import markets.orders._


/** Abstract class defining the interface for a `MatchingEngine`. */
abstract class AbstractMatchingEngine {

  def askOrderBook: AbstractOrderBook[AskOrder]

  def bidOrderBook: AbstractOrderBook[BidOrder]

  /** Partial function defining the logic for matching an `AskOrder` with a `BidOrder`.
    *
    * @note default logic neither adds an unmatched `AskOrder` to the `askOrderBook`, nor removes a matched `BidOrder`
    *       from the `bidOrderBook` as the desired timing of `add` (`remove`) operations can depend on higher level
    *       implementation details.
    */
  def matchAskOrder: PartialFunction[AskOrder, Option[BidOrder]] = defaultAskOrderMatchingLogic

  /** Partial function defining the logic for matching a `BidOrder` with an `AskOrder`.
    *
    * @note default logic neither adds an unmatched `BidOrder` to the `bidOrderBook`, nor removes a matched `AskOrder`
    *       from the `askOrderBook` as the desired timing of `add` (`remove`) operations can depend on higher level
    *       implementation details.
    */
  def matchBidOrder: PartialFunction[BidOrder, Option[AskOrder]] = defaultBidOrderMatchingLogic

  /* Default logic for matching an `AskOrder` with a `BidOrder`. */
  private[this] val defaultAskOrderMatchingLogic: PartialFunction[AskOrder, Option[BidOrder]] = {
    case order: AggressiveAskOrder => bidOrderBook.find(order.predicate)
    case order: ExhaustiveAskOrder => bidOrderBook.filter(order.predicate) match {
      case Some(bidOrders) => bidOrders.reduceOption(order.operator)
      case None => None
    }
  }

  /* Default logic for matching a `BidOrder` with an `AskOrder`. */
  private[this] final val defaultBidOrderMatchingLogic: PartialFunction[BidOrder, Option[AskOrder]] = {
    case order: AggressiveBidOrder => askOrderBook.find(order.predicate)
    case order: ExhaustiveBidOrder => askOrderBook.filter(order.predicate) match {
      case Some(askOrders) => askOrders.reduceOption(order.operator)
      case None => None
    }
  }

}
