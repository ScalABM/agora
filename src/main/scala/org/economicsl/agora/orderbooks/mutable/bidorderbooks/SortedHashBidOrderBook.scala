package org.economicsl.agora.orderbooks.mutable.bidorderbooks

import java.util.UUID

import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.bid.BidOrder

import scala.collection.mutable


class SortedHashBidOrderBook[B <: BidOrder](tradable: Tradable)(implicit ordering: Ordering[B])
  extends SortedBidOrderBook[B](tradable) {

  /* Underlying sorted collection of `Order` instances. */
  protected val sortedOrders: mutable.TreeSet[B] = mutable.TreeSet.empty[B](ordering)

  /* Underlying collection of `Order` instances. */
  protected val existingOrders: mutable.Map[UUID, B] = mutable.HashMap.empty[UUID, B]

}
