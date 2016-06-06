package markets.engines.orderbooks

import markets.orders.BidOrder


trait BidOrderBook[+CC <: Iterable[BidOrder]] extends OrderBook[BidOrder, CC]
