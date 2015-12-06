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

import java.util.UUID

import markets.clearing.strategies.TestPriceFormationStrategy
import markets.clearing.engines.matches.Match
import markets.orders.Order

import scala.collection.immutable


/** A BrokenMatchingEngine just stores incoming orders and never generates matches. */
class BrokenMatchingEngine extends MatchingEngineLike with TestPriceFormationStrategy {

  protected var orderBook = immutable.Set.empty[Order]

  /** A `BrokenMatchingEngine` always fails to findMatch orders. */
  def findMatch(incomingOrder: Order): Option[immutable.Iterable[Match]] = {
    orderBook += incomingOrder  // SIDE EFFECT!
    None
  }

  def removeOrder(uuid: UUID): Option[Order] = {
    orderBook.find(order => order.uuid == uuid) match {
      case result @ Some(order) =>
        orderBook -= order
        result
      case None => None
    }
  }

}