package org.economicsl.agora.orderbooks.mutable.bidorderbooks

import org.economicsl.agora.orderbooks.mutable.{ExistingOrders, OrderBook}
import org.economicsl.agora.tradables.Tradable
import org.economicsl.agora.tradables.orders.bid.BidOrder


abstract class BidOrderBook[B <: BidOrder](val tradable: Tradable) extends OrderBook[B](tradable) with ExistingOrders[B]
