package org.economicsl.agora.orderbooks.mutable.askorderbooks

import org.economicsl.agora.orderbooks.mutable.{ExistingOrders, OrderBook}
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.ask.AskOrder


/** Abstract base class for an `OrderBook` containing `AskOrder` instances.
  *
  * @param tradable
  * @tparam A
  */
abstract class AskOrderBook[A <: AskOrder](val tradable: Tradable) extends OrderBook[A](tradable) with ExistingOrders[A]

