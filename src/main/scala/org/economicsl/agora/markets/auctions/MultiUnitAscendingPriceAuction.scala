package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.tradables.MultiUnit
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


trait MultiUnitAscendingPriceAuction[B <: LimitBidOrder with Persistent with MultiUnit]
  extends MultiUnitAuction[B] with AscendingBidOrders[B]
