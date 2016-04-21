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


/** Class representing a market regulatory agency.
  *
  * @param participants
  * @param markets
  * @note The `MarketRegulatorActor` supervises a collection of market participants as well as a
  *       collection of markets.  When there are no longer any market participants, the
  *       `MarketRegulatorActor` shutdowns all the markets and terminates the actor system.
  */
class MarketRegulatorActor(participants: Iterable[ActorRef],
                           markets: Iterable[ActorRef]) extends StackableActor {

  participants.foreach(participant => context.watch(participant))
  markets.foreach(market => context.watch(market))

  wrappedBecome(marketRegulatorBehavior)

  def marketRegulatorBehavior: Receive = {
    case Terminated(entity) if _participants.contains(entity) =>
      _participants -= entity
      if (_participants.isEmpty) {
        _markets.foreach(market => market tell(PoisonPill, self))
      }
    case Terminated(entity) if _markets.contains(entity) =>
      _markets -= entity
      if (_markets.isEmpty) {
        context.system.terminate()
      }
  }

  // Internally represent regulated entities as Sets
  private[this] var _participants = participants.toSet
  private[this] var _markets = markets.toSet

}


object MarketRegulatorActor {

  def props(participants: Iterable[ActorRef], markets: Iterable[ActorRef]): Props = {
    Props(new MarketRegulatorActor(participants, markets))
  }

}
