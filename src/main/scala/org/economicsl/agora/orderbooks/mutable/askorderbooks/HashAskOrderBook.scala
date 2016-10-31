package org.economicsl.agora.orderbooks.mutable.askorderbooks

import java.util.UUID

import org.economicsl.agora.generics
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.ask.AskOrder

import scala.collection.mutable


class HashAskOrderBook[A <: AskOrder](tradable: Tradable) extends AskOrderBook[A](tradable) {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders: mutable.Map[UUID, A] = mutable.HashMap.empty[UUID, A]

}
