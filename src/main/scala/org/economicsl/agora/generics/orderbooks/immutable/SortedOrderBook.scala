package org.economicsl.agora.generics.orderbooks.immutable

import java.util.UUID

import org.economicsl.agora.tradables.orders.Order

import scala.collection.immutable


trait SortedOrderBook[O <: Order, +CC <: immutable.Map[UUID, O]] extends OrderBook[O, CC] with SortedOrders[O]
