package markets.clearing

import java.util.UUID

import markets.Message
import markets.clearing.engines.Matching


case class Fill(matchedOrders: Matching, timestamp: Long, uuid: UUID) extends Message
