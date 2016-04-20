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
package markets

import akka.actor.{ActorRef, PoisonPill, Props, Terminated}

import markets.tradables.Tradable

import scala.collection.mutable


class MarketRegulator(brokerage: ActorRef,
                      markets: mutable.Map[Tradable, ActorRef]) extends StackableActor {

  wrappedBecome(marketRegulatorBehavior)

  /** A MarketRegulator maintains a list of market participants that it monitors. */
  val regulatedEntities = mutable.Set.empty[ActorRef]

  // MarketRegulator monitors both the brokerage and the markets...
  context.watch(brokerage); regulatedEntities += brokerage
  markets.foreach{ case (_, market) => context.watch(market); regulatedEntities += market }

  def marketRegulatorBehavior: Receive = {
    case Terminated(entity) =>
      regulatedEntities -= entity
      if (entity == brokerage) {
        markets.foreach { case (_, market) => market tell(PoisonPill, self) }
      } else if (regulatedEntities.isEmpty) {
        context.system.terminate()
      } else {
        // do nothing!
      }
  }

}


object MarketRegulator {

  def props(broker: ActorRef, markets: mutable.Map[Tradable, ActorRef]): Props = {
    Props(new MarketRegulator(broker, markets))
  }

}
