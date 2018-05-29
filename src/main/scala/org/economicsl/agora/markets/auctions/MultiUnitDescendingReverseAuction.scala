package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.tradables.MultiUnit
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder


trait MultiUnitDescendingReverseAuction[A <: LimitAskOrder with Persistent with MultiUnit]
  extends MultiUnitReverseAuction[A] with DescendingAskOrders[A]
