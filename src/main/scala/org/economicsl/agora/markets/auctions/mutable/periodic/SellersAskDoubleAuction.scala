package org.economicsl.agora.markets.auctions.mutable.periodic

import org.economicsl.agora.markets.tradables.Tradable


/** Class implementing a periodic, "Seller's Ask" Double Auction.
  *
  * @param tradable
  */
class SellersAskDoubleAuction(tradable: Tradable) extends KDoubleAuction(0, tradable)

