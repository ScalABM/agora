package org.economicsl.agora.markets.auctions.matching

import org.economicsl.agora.markets.auctions.orderbooks.OrderBook
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


trait MatchingRule[-O1 <: Order, OB <: OrderBook[O2, OB], +O2 <: Order with Persistent]
  extends ((O1, OB) => Option[O2])
