package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.GenericBidOrderBook
import markets.orders.BidOrder

import scala.collection.immutable


trait GenericImmutableBidOrderBook[+CC <: immutable.Iterable[BidOrder]]
  extends GenericBidOrderBook[CC]
