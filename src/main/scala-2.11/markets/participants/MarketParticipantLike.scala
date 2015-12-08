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
package markets.participants

import akka.actor.ActorRef

import java.util.UUID

import markets.{Accepted, Add, BaseActor, Canceled, Rejected, Remove}
import markets.tradables.Tradable

import scala.collection.immutable


trait MarketParticipantLike {
  this: BaseActor =>

  protected var markets: immutable.Map[Tradable, ActorRef]

  protected var outstandingOrders: immutable.Set[UUID]

  def marketParticipantBehavior: Receive = {
    case Accepted(order, _, _) =>
      outstandingOrders += order.uuid
    case Canceled(order, _, _) =>
      outstandingOrders -= order.uuid
    case Add(market, _, tradable, _) =>
      markets = markets + ((tradable, market))
    case Remove(_, _, tradable, _) =>
      markets = markets - tradable
    case message: Rejected =>
      log.debug(message.toString)
  }

}
