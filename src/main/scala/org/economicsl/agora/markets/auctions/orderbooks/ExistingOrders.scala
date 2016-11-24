package org.economicsl.agora.markets.auctions.orderbooks

import java.util.UUID

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


trait ExistingOrders[+O <: Order with Persistent] {

  protected def existingOrders: collection.GenMap[UUID, O]

}
