package org.economicsl.agora.orderbooks.mutable

import java.util.UUID

import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.Order

import scala.collection.mutable


/** Class that implements a `mutable.OrderBook` using a `mutable.Map` as the underlying collection for storing `Order`
  * instances.
  *
  * @param tradable
  * @tparam O
  */
class HashOrderBook[O <: Order](val tradable: Tradable) extends OrderBook[O] with ExistingOrders[O] {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders: mutable.Map[UUID, O] = mutable.HashMap.empty[UUID, O]

}
