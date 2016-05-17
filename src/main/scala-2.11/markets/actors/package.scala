package markets

import akka.actor.ActorRef
import akka.agent.Agent

import java.util.UUID

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


package object actors {

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that a
    * previously submitted order has been filled.
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
