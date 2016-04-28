package markets.engines.orderbooks

import markets.orders.BidOrder


trait GenericBidOrderBook[+CC <: Iterable[BidOrder]] extends GenericOrderBook[BidOrder, CC]
