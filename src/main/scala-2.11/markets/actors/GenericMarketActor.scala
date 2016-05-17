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

import akka.actor.ActorRef
import akka.agent.Agent

import markets.engines.GenericMatchingEngine
import markets.orders.{AskOrder, BidOrder, Order}
import markets.tickers.Tick
import markets.tradables.Tradable


trait GenericMarketActor[+CC1 <: Iterable[AskOrder], +CC2 <: Iterable[BidOrder]]
  extends StackableActor {

  def settlementMechanism: ActorRef

  def ticker: Agent[Tick]

  def tradable: Tradable

  def matchingEngine: GenericMatchingEngine[CC1, CC2]

  override def receive: Receive = {
    case order: Order if !(order.tradable == tradable) =>
      sender() tell(Rejected(order), self)
    case Cancel(order) =>
      val result = matchingEngine.pop(order)
      result match {
        case Some(residualOrder) =>
          sender() tell(Canceled(residualOrder), self)
        case None =>  // @todo notify sender that order was not canceled?
      }
    case message => super.receive(message)
  }

}

