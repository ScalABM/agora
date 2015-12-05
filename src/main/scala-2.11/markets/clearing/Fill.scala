package markets.clearing

import java.util.UUID

import markets.Message
import markets.clearing.engines.matches.Match


case class Fill(matchedOrders: Match, timestamp: Long, uuid: UUID) extends Message
