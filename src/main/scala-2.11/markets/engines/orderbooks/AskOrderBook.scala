package markets.engines.orderbooks

import markets.orders.AskOrder


trait AskOrderBook[+CC <: Iterable[AskOrder]] extends OrderBook[AskOrder, CC]
