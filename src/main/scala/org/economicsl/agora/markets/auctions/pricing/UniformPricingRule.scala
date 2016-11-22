package org.economicsl.agora.markets.auctions.pricing

import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.Price
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** Trait defining a uniform pricing rule. */
trait UniformPricingRule[A <: AskOrder with Persistent, AB <: AskOrderBook[A],
                         B <: BidOrder with Persistent, BB <: BidOrderBook[B]]
  extends ((AB, BB) => Price)
