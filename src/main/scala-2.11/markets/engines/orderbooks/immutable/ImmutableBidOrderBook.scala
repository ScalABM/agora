package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.BidOrderBook
import markets.orders.BidOrder

import scala.collection.immutable


trait ImmutableBidOrderBook[+CC <: immutable.Iterable[BidOrder]]
  extends BidOrderBook[CC]
