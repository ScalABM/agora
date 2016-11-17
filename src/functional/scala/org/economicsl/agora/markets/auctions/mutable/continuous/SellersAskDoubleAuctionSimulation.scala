package org.economicsl.agora.markets.auctions.mutable.continuous

import java.util.UUID

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.{AllowedSolution, BracketingNthOrderBrentSolver}
import org.apache.commons.math3.distribution
import org.economicsl.agora.markets.tradables.{Price, Tradable}
import org.economicsl.agora.markets.tradables.orders.ask.PersistentLimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.PersistentLimitBidOrder


/** Simulation of the continuous 0-Double Auction mechanism of Satterthwaite and Williams (JET, 1989). */
object SellersAskDoubleAuctionSimulation {


  /** Class implementing the equilibrium trading strategy for a buyer participating in a "Seller's Ask" Double Auction.
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param issuer the `UUID` of the trader submitting the `LimitBidOrder`.
    * @param reservationValue the private reservation value for the `issuer`.
    * @param sellerValuations the distribution of seller valuations.
    * @note because a buyer participating in a "Seller's Ask" Double Auction can not affect the price, its dominant
    *       strategy is to reveal its private reservation value when submitting its `LimitBidOrder`. Further details of
    *       the equilibrium trading strategy are described in theorem 3.3 from Satterthwaite and Williams (JET, 1989).
    */
  protected[auctions] class BuyerEquilibriumTradingRule(buyerValuations: distribution.RealDistribution,
                                                        issuer: UUID,
                                                        reservationValue: Double,
                                                        sellerValuations: distribution.RealDistribution)
    extends KDoubleAuctionSimulation.TradingRule[PersistentLimitBidOrder](buyerValuations, sellerValuations) {

    def apply(tradable: Tradable): PersistentLimitBidOrder = {
      val limit = Price(reservationValue)
      PersistentLimitBidOrder(issuer, limit, 1, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

  }

  /** Class implementing the equilibrium trading strategy for a seller participating in a "Seller's Ask" Double Auction.
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param issuer the `UUID` of the trader submitting the `LimitAskOrder`.
    * @param reservationValue the private reservation value for the `issuer`.
    * @param sellerValuations the distribution of seller valuations.
    * @note details of the equilibrium trading strategy for a seller participating in a "Seller's Ask" Double Auction
    *       are described in theorem 3.3 from Satterthwaite and Williams (JET, 1989).
    */
  protected[auctions] class SellerEquilibriumTradingRule(buyerValuations: distribution.RealDistribution,
                                                         issuer: UUID,
                                                         reservationValue: Double,
                                                         sellerValuations: distribution.RealDistribution)
    extends KDoubleAuctionSimulation.TradingRule[PersistentLimitAskOrder](buyerValuations, sellerValuations) {

    def apply(tradable: Tradable): PersistentLimitAskOrder = {
      val limit = Price(solver.solve(100, F, 0, 1, AllowedSolution.BELOW_SIDE))
      PersistentLimitAskOrder(issuer, limit, 1, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

    /** Seller's equilibrium limit price should equate its reservation value with a buyer's virtual reservation value. */
    private[this] val F = new UnivariateFunction {
      def value(x: Double): Double = {
        val buyerVirtualReservationValue = virtualReservationValue(buyerInverseHazardRate, x)
        buyerVirtualReservationValue - reservationValue
      }
    }

    private[this] val solver = new BracketingNthOrderBrentSolver(1e-9, 1e-12, 5)

  }



}
