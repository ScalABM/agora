package org.economicsl.agora.orderbooks.mutable.askorderbooks

import java.util.UUID

import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.ask.AskOrder

import scala.collection.mutable


class SortedHashAskOrderBook[A <: AskOrder](tradable: Tradable)(implicit ordering: Ordering[A])
  extends SortedAskOrderBook[A](tradable) {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders: mutable.Map[UUID, A] = mutable.HashMap.empty[UUID, A]

  /* Underlying sorted collection of `Order` instances. */
  protected val sortedOrders: mutable.TreeSet[A] = mutable.TreeSet.empty[A](ordering)

}
