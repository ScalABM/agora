package markets.orderbooks.mutable

import java.util.UUID

import markets.tradables.orders.ask.AskOrder

import scala.collection.mutable


/** Trait defining the interface for a `mutable.AskOrderBook`.
  * 
  * @tparam A
  * @tparam CC
  */
trait AskOrderBook[A <: AskOrder, CC <: mutable.Map[UUID, A]] extends OrderBook[A, CC]