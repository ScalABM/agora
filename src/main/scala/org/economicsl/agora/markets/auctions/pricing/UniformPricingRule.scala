package org.economicsl.agora.markets.auctions.pricing

import org.economicsl.agora.markets.auctions.orderbooks
import org.economicsl.agora.markets.tradables.Price
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** Trait defining a uniform pricing rule. */
trait UniformPricingRule[A <: AskOrder with Persistent, B <: BidOrder with Persistent]
  extends ((orderbooks.OrderBook[A], orderbooks.OrderBook[B]) => Price)
