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
package markets.engines

import markets.orders.{BidOrder, AskOrder, Order}

import scala.collection.{immutable, mutable}


/** A BrokenMatchingEngine just stores incoming orders and never generates matches. */
class BrokenMatchingEngine extends MutableMatchingEngine {

  protected val _askOrderBook = mutable.Set.empty[AskOrder]

  protected val _bidOrderBook = mutable.Set.empty[BidOrder]

  /** A sorted collection of ask orders.
    * @note This is an immutable view into a mutable private collection.
    */
  def askOrderBook: immutable.Set[AskOrder] = {
    _askOrderBook.toSet
  }

  /** A sorted collection of bid orders.
    * @note This is an immutable view into a mutable private collection.
    */
  def bidOrderBook: immutable.Set[BidOrder] = {
    _bidOrderBook.toSet
  }

  /** A `BrokenMatchingEngine` always fails to findMatch orders. */
  def findMatch(incoming: Order): Option[immutable.Queue[Matching]] = {
    incoming match {
      case order: AskOrder =>
        _askOrderBook += order // SIDE EFFECT!
        None
      case order: BidOrder =>
        _bidOrderBook += order // SIDE EFFECT!
        None
    }
  }

  def remove(order: Order): Option[Order] = {
    order match {
      case _: AskOrder =>
        _askOrderBook.find(o => o.uuid == order.uuid) match {
          case result@Some(residualOrder) =>
            _askOrderBook.remove(residualOrder) // SIDE EFFECT!
            result
          case _ => None
        }
      case _: BidOrder =>
        _bidOrderBook.find(o => o.uuid == order.uuid) match {
          case result@Some(residualOrder) =>
            _bidOrderBook.remove(residualOrder) // SIDE EFFECT!
            result
          case _ => None
        }
    }
  }

}