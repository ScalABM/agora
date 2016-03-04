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
import akka.actor.ActorRef
import akka.agent.Agent

import java.util.UUID

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


package object markets {

  /** Base trait for all messages. */
  trait Message {

    def timestamp: Long

    def uuid: UUID

  }


  /** Base trait for representing contracts. */
  trait Contract extends Message {

    /** The actor for whom the `Contract` is a liability. */
    def issuer: ActorRef

    /** The actor for whom the `Contract` is an asset. */
    def counterparty: Option[ActorRef]

  }

  /** Message sent to some `MarketParticipant` actor indicating that the actor should add a
    * particular market to the collection of markets on which it trades.
    * @param market
    * @param ticker
    * @param timestamp
    * @param tradable
    * @param uuid
    */
  case class Add(market: ActorRef,
                 ticker: Agent[Tick],
                 timestamp: Long,
                 tradable: Tradable,
                 uuid: UUID) extends Message

  /** Message sent to some `MarketParticipant` actor indicating that the actor should remove a
    * particular market from the collection of markets on which it trades.
    * @param timestamp
    * @param tradable
    * @param uuid
    */
  case class Remove(timestamp: Long, tradable: Tradable, uuid: UUID) extends Message

  /** Message sent from a `MarketParticipant` actor to some `MarketActor` indicating that it
    * wishes to cancel a previously submitted order.
    * @param order
    * @param timestamp
    * @param uuid
    */
  case class Cancel(order: Order, timestamp: Long, uuid: UUID) extends Message

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been canceled.
    * @param order
    * @param timestamp
    * @param uuid
    */
  case class Canceled(order: Order, timestamp: Long, uuid: UUID) extends Message

  /** Message sent from ??? to some `MarketParticipant` actor indicating that a previously
    * submitted order has been filled.
    * @param order
    * @param residual
    * @param timestamp
    * @param uuid
    */
  case class Filled(order: Order, residual: Option[Order], timestamp: Long, uuid: UUID) extends Message

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been rejected.
    * @param order
    * @param timestamp
    * @param uuid
    */
  case class Rejected(order: Order, timestamp: Long, uuid: UUID) extends Message

}
