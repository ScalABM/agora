package markets.engines.orderbooks.immutable

import markets.engines.orderbooks.AskOrderBook
import markets.orders.AskOrder

import scala.collection.immutable

trait ImmutableAskOrderBook[+CC <: immutable.Iterable[AskOrder]]
  extends AskOrderBook[CC]
