package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.GenericAskOrderBook
import markets.orders.AskOrder

import scala.collection.immutable

trait GenericImmutableAskOrderBook[+CC <: immutable.Iterable[AskOrder]]
  extends GenericAskOrderBook[CC]
