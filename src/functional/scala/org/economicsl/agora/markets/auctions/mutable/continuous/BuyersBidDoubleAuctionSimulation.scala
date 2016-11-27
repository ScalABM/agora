/*
Copyright 2016 ScalABM

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.economicsl.agora.markets.auctions.mutable.continuous

import java.util.UUID

import org.economicsl.agora.markets.auctions.matching.FindFirstAcceptableOrder
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.{LimitAskOrder, PersistentLimitAskOrder}
import org.economicsl.agora.markets.tradables.orders.bid.{LimitBidOrder, PersistentLimitBidOrder}
import org.economicsl.agora.markets.tradables._
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.{AllowedSolution, BracketingNthOrderBrentSolver}
import org.apache.commons.math3.{distribution, random, stat}

import scala.util.Random


/** Simulation of the continuous 1-Double Auction mechanism of Satterthwaite and Williams (JET, 1989). */
object BuyersBidDoubleAuctionSimulation extends App {

  // Create something to store simulated prices
  val summaryStatistics = new stat.descriptive.SummaryStatistics()

  // Create a single source of randomness for simulation in order to minimize indeterminacy
  val seed = 42
  val prng = new random.MersenneTwister(seed)

  // Create a collection of traders each with it own trading rule
  val numberTraders = 100
  val traders = for { i <- 1 to numberTraders } yield UUID.randomUUID()

  // valuation distributions are assumed common knowledge
  val buyerValuations = new distribution.UniformRealDistribution()
  val sellerValuations = new distribution.UniformRealDistribution()

  val tradingRules = traders.map { trader =>

    // reservation value is private information
    val reservationValue = prng.nextDouble()

    if (prng.nextDouble() <= 0.5) {
      Left(new SellerEquilibriumTradingRule(buyerValuations, trader, reservationValue, sellerValuations))
    } else {
      Right(new BuyerEquilibriumTradingRule(buyerValuations, trader, reservationValue, sellerValuations))
    }

  }

  /** Define the a "Buyer's Bid" Double Auction. */
  val auction = {

    val tradable = TestTradable()
    val askOrderMatchingRule = FindFirstAcceptableOrder[LimitAskOrder with Quantity, LimitBidOrder with Persistent with Quantity]()
    val bidOrderMatchingRule = FindFirstAcceptableOrder[LimitBidOrder with Quantity, LimitAskOrder with Persistent with Quantity]()
    BuyersBidDoubleAuction(askOrderMatchingRule, bidOrderMatchingRule, tradable)

  }

  // simple for loop that actually runs a simulation...
  for { t <- 0 until 1000} {

    // ...generate a batch of orders...this step is trivially parallel!
    val orders = Random.shuffle(tradingRules).map {
      case Left(sellerTradingRule) => Left(sellerTradingRule(auction.tradable))
      case Right(buyerTradingRule) => Right(buyerTradingRule(auction.tradable))
    }

    // ...feed the orders into the auction mechanism...amount of parallelism in this step varies!
    val fills = orders.flatMap {
      case Left(limitAskOrder) => auction.fill(limitAskOrder)
      case Right(limitBidOrder) => auction.fill(limitBidOrder)
    }

    // ...collect the generated prices...this step is trivially parallel!
    fills.foreach(fill => summaryStatistics.addValue(fill.price.value))

  }

  // ...print to screen for reference...
  println(summaryStatistics.toString)


  /** Class implementing the equilibrium trading strategy for a buyer participating in a "Buyer's Bid" Double Auction.
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param issuer the `UUID` of the trader submitting the `LimitBidOrder`.
    * @param reservationValue the private reservation value for the `issuer`.
    * @param sellerValuations the distribution of seller valuations.
    * @note the details of the equilibrium trading strategy for a buyer participating in a "Buyer's Bid" Double Auction
    *       are described in theorem 3.3 from Satterthwaite and Williams (JET, 1989).
    */
  protected[auctions] class BuyerEquilibriumTradingRule(buyerValuations: distribution.RealDistribution,
                                                        issuer: UUID,
                                                        reservationValue: Double,
                                                        sellerValuations: distribution.RealDistribution)
    extends KDoubleAuctionSimulation.EquilibriumTradingRule[PersistentLimitBidOrder with SingleUnit](buyerValuations, sellerValuations) {

    def apply(tradable: Tradable): PersistentLimitBidOrder with SingleUnit = {
      val limit = Price(solver.solve(100, F, 0, 1, AllowedSolution.BELOW_SIDE))
      PersistentLimitBidOrder(issuer, limit, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

    /** Buyer's equilibrium limit price should equate its reservation value with a seller's virtual reservation value. */
    private[this] val F = new UnivariateFunction {
      def value(x: Double): Double = {
        val sellerVirtualReservationValue = virtualReservationValue(sellerInverseHazardRate, x)
        sellerVirtualReservationValue - reservationValue
      }
    }

    private[this] val solver = new BracketingNthOrderBrentSolver(1e-9, 1e-12, 5)

  }


  /** Class implementing the equilibrium trading strategy for a seller participating in a "Buyer's Bid" Double Auction.
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param issuer the `UUID` of the trader submitting the `LimitAskOrder`.
    * @param reservationValue the private reservation value for the `issuer`.
    * @param sellerValuations the distribution of seller valuations.
    * @note because a seller participating in a "Buyer's Bid" Double Auction can not affect the price, its dominant
    *       strategy is to reveal its private reservation value when submiting its `LimitAskOrder`. Further details of
    *       the equilibrium trading strategy are described in theorem 3.3 from Satterthwaite and Williams (JET, 1989).
    */
  protected[auctions] class SellerEquilibriumTradingRule(buyerValuations: distribution.RealDistribution,
                                                         issuer: UUID,
                                                         reservationValue: Double,
                                                         sellerValuations: distribution.RealDistribution)
    extends KDoubleAuctionSimulation.EquilibriumTradingRule[PersistentLimitAskOrder with SingleUnit](buyerValuations, sellerValuations) {

    def apply(tradable: Tradable): PersistentLimitAskOrder with SingleUnit = {
      val limit = Price(reservationValue)
      PersistentLimitAskOrder(issuer, limit, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

  }

}
