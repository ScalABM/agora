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
package markets.actors

import markets.Fill
import markets.engines.GenericCDAMatchingEngine
import markets.orders.{AskOrder, BidOrder, Order}
import markets.tickers.Tick


trait GenericCDAMarketActor[+CC1 <: Iterable[AskOrder], +CC2 <: Iterable[BidOrder]]
  extends GenericMarketActor[CC1, CC2] {

  def askOrdering: Ordering[AskOrder]

  def bidOrdering: Ordering[BidOrder]

  def initialPrice: Long

  def matchingEngine: GenericCDAMatchingEngine[CC1, CC2]

  override def receive: Receive = {
    case order: Order if order.tradable == tradable =>
      sender() tell(Accepted(order), self)
      matchingEngine.findMatch(order) match {
        case Some(matchings) =>
          matchings.foreach { matching =>
            val fill = Fill.fromMatching(matching, timestamp(), uuid())
            val tick = Tick.fromFill(fill)
            ticker.send(tick) // SIDE EFFECT!
            settlementMechanism tell(fill, self)
          }
        case None => // nothing to do!
      }
  }
}

