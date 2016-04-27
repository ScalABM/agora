package markets.engines.orderbooks

import markets.orders.AskOrder


trait GenericAskOrderBook[+CC <: Iterable[AskOrder]] extends GenericOrderBook[AskOrder, CC]
