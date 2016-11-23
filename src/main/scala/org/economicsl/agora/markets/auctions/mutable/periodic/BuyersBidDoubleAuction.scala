package org.economicsl.agora.markets.auctions.mutable.periodic

import org.economicsl.agora.markets.tradables.Tradable


/** Class implementing a periodic, "Buyer's Bid" Double Auction.
  *
  * @param tradable
  */
class BuyersBidDoubleAuction(tradable: Tradable) extends KDoubleAuction(1, tradable)

