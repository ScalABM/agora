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
package markets.engines.immutable

import markets.engines.{Fill, GenericMatchingEngine}
import markets.orders.{AskOrder, BidOrder, Order}

import scala.collection.immutable.{Queue, Set}



/** Stub implementation of a `GenericMutableMatchingEngine` for testing purposes.
  *
  * @note A `TestMutableMatchingEngine` stores all incoming orders and never generates matches.
  */
class TestImmutableMatchingEngine
  extends GenericMatchingEngine[Set[AskOrder], Set[BidOrder]] {

  val askOrderBook = ??? // new ImmutableSetAskOrderBook

  val bidOrderBook = ??? // new ImmutableSetBidOrderBook

  def findMatch(incoming: Order): Option[Queue[Fill]] = {
    incoming match {
      case order: AskOrder =>
        //askOrderBook.add(order) // SIDE EFFECT!
      case order: BidOrder =>
        //bidOrderBook.add(order) // SIDE EFFECT!
    }
    None
  }

}