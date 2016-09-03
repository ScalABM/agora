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
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable


/** Abstract class defining the interface for a `MatchingEngine`.
  *
  * @param tradable a `MatchingEngine` matches `AskOrder` instances with `BidOrder` instances for the same `Tradable`.
  */
abstract class AbstractMatchingEngine(val tradable: Tradable) {

  /** Finds a matching `AskOrder` for a particular `BidOrder`.
    *
    * @param order the `BidOrder` that should be matched.
    * @return `None` if no suitable `AskOrder` can be found; `Some(askOrder)` otherwise.
    */
  def findMatchFor(order: BidOrder): Option[AskOrder] = {
    require(order.tradable == tradable)
    order.find match {
      case Some(predicate) => askOrderBook.find(predicate) match {
        case Some(askOrder) => askOrderBook.remove(askOrder.uuid)
        case None => None
      }
      case None => None
    }
  }

  /** Finds a matching `BidOrder` for a particular `AskOrder`.
    *
    * @param order the `AskOrder` that should be matched.
    * @return `None` if no suitable `BidOrder` can be found; `Some(bidOrder)` otherwise.
    */
  def findMatchFor(order: AskOrder): Option[BidOrder] = {
    require(order.tradable == tradable)
    order.find match {
      case Some(predicate) => bidOrderBook.find(predicate) match {
        case Some(bidOrder) => bidOrderBook.remove(bidOrder.uuid)
        case None => None
      }
      case None => None
    }
  }

  protected[engines] def askOrderBook: AbstractOrderBook[AskOrder]

  protected[engines] def bidOrderBook: AbstractOrderBook[BidOrder]

}
