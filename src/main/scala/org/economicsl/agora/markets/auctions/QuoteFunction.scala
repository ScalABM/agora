package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.auctions.mutable.orderbooks.OrderBook
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


trait QuoteFunction[O <: Order with Persistent, OB <: OrderBook[O]] extends ((OB) => PriceQuote)
