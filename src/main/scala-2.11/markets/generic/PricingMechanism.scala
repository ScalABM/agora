package markets.generic

import java.util.UUID

import markets.orders.{AskOrder, BidOrder}

/** Trait defining the interface for a `PricingMechanism`.
  *
  * An ideal pricing mechanism would satisfy the following properties:
  *
  * 1. Individual Rationality (IR): no person should lose from joining the auction. In particular, for every trading
  * buyer: p <= B, and for every trading seller: p >= S.
  * 2. Balanced Budget (BB) comes in two flavors:
  * * Strong balanced budget (SBB): all monetary transfers must be done between buyers and sellers; the auctioneer should
  * not lose or gain money.
  * * Weak balanced budget (WBB): the auctioneer should not lose money, but may gain money.
  * 3. Truthfulness (TF), also called Incentive compatibility (IC) or "strategy-proofness" also comes in two flavors:
  * * Dominant-strategy-incentive-compatibility (DSIC), which means that reporting the true value
  * should be a dominant strategy for all players (i.e., a player should not be able to gain by spying over other
  * players and trying to find an 'optimal' declaration which is different from his true value, regardless of how the
  * other players play.
  * * Nash-equilibrium-incentive-compatibility (NEIC), which means that there exists a Nash
  * equilibrium in which all players report their true valuations (i.e., if all players but one are truthful, it is
  * best for the remaining player to also be truthful).
  * 4. Economic efficiency (EE): the total social welfare (the sum of the values of all players) should be the best
  * possible. In particular, this means that, after all trading has completed, the items should be in the hands of
  * those that value them the most.
  *
  * Unfortunately, it is not possible to achieve all these requirements in the same mechanism (see Myerson–Satterthwaite
  * theorem). But there are mechanisms that satisfy some of them.
  */
trait PricingMechanism[O1 <: AskOrder, O2 <: BidOrder, OB1 <: OrderBook[O1, collection.GenMap[UUID, O1]], OB2 <: OrderBook[O2, collection.GenMap[UUID, O2]]] {

  def formPrice(askOrderBook: OB1, bidOrderBook: OB2)

}
