package org.economicsl.agora.orderbooks.mutable.bidorderbooks

import java.util.UUID

import org.economicsl.agora.generics
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.bid.BidOrder

import scala.collection.mutable


class HashBidOrderBook[B <: BidOrder](tradable: Tradable) extends BidOrderBook[B](tradable) {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders: mutable.Map[UUID, B] = mutable.HashMap.empty[UUID, B]

}
