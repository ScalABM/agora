package org.economicsl.agora.markets.auctions.orderbooks

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


trait ExistingOrders[K, +O <: Order with Persistent, +CC <: collection.GenIterable[(K, O)]] {

  def existingOrders: CC

}
