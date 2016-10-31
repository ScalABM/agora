package org.economicsl.agora.orderbooks.mutable

import java.util.UUID

import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.Order

import scala.collection.mutable


class SortedHashOrderBook[O <: Order](val tradable: Tradable)(implicit ordering: Ordering[O]) extends SortedOrderBook[O] {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders: mutable.Map[UUID, O] = mutable.HashMap.empty[UUID, O]

  /* Underlying sorted collection of `Order` instances. */
  protected val sortedOrders: mutable.TreeSet[O] = mutable.TreeSet.empty[O](ordering)

}
