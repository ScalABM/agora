package markets

import akka.actor.ActorRef
import akka.agent.Agent

import java.util.UUID

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable

/**
  * Created by drpugh on 4/27/16.
  */
package object actors {

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

  /** Message sent from ??? to some `MarketParticipant` actor indicating that a previously
    * submitted order has been filled.
    * @param order
    * @param residual
    * @param timestamp
    * @param uuid
    */
  case class Filled(order: Order, residual: Option[Order], timestamp: Long, uuid: UUID) extends Message


  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been accepted.
    *
    * @param order
    * @param timestamp
    * @param uuid
    */
  case class Accepted(order: Order, timestamp: Long, uuid: UUID) extends Message

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

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been rejected.
    * @param order
    * @param timestamp
    * @param uuid
    */
  case class Rejected(order: Order, timestamp: Long, uuid: UUID) extends Message

}
