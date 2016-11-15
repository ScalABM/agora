package org.economicsl.agora.markets.auctions.mutable.continuous

import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricing
import org.economicsl.agora.markets.tradables.Tradable

/** Class implementing a "Buyer's bid" double auction similar to that described in Satterthwaite and Williams (1989, RES).
  *
  * @param tradable all `Order` instances must be for the same `Tradable`.
  * @note in a "Buyer's bid" double auction, the price for each `Fill` is determined by the `LimitBidOrder` and
  *       therefore all of the profit from each trade accrues to the issuer of the `LimitAskOrder`.  The issuer of the
  *       `LimitAskOrder` can not influence the `Fill` price and should truthfully reveal is private reservation value
  *       when issuing the `LimitAskOrder`. The issuer of the 'LimitBidOrder', however, clearly has an incentive to bid
  *       strictly less than its private reservation value.
  */
class BuyersBidDoubleAuction(tradable: Tradable) extends DoubleAuction(WeightedAveragePricing(0.0), WeightedAveragePricing(1.0), tradable)
