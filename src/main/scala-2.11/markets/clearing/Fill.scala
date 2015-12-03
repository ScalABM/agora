package markets.clearing

import markets.Message
import markets.clearing.engines.matches.Match


case class Fill(matchedOrders: Match, timestamp: Long) extends Message
