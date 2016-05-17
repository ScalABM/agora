package markets

import markets.orders.Order


package object actors {

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that a
    * previously submitted order has been filled.
    * @param order
    * @param residual
    */
  case class Filled(order: Order, residual: Option[Order])


  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been accepted.
    *
    * @param order
    */
  case class Accepted(order: Order)

  /** Message sent from a `MarketParticipant` actor to some `MarketActor` indicating that it
    * wishes to cancel a previously submitted order.
    * @param order
    */
  case class Cancel(order: Order)

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been canceled.
    * @param order
    */
  case class Canceled(order: Order)

  /** Message sent from a `MarketActor` to some `MarketParticipant` actor indicating that its
    * order has been rejected.
    * @param order
    */
  case class Rejected(order: Order)

}
