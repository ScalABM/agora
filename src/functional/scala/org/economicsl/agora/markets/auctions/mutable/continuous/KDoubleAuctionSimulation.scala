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

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, TestTradable, Tradable}
import org.economicsl.agora.markets.tradables.orders.ask.{LimitAskOrder, PersistentLimitAskOrder}
import org.economicsl.agora.markets.tradables.orders.bid.{LimitBidOrder, PersistentLimitBidOrder}
import org.apache.commons.math3.{distribution, random, stat}
import org.economicsl.agora.markets.auctions.matching.FindFirstAcceptableOrder

import scala.util.Random


/** Simulation of the continuous k-Double Auction mechanism of Satterthwaite and Williams (JET, 1989). */
object KDoubleAuctionSimulation extends App {

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

  /** Define the a k-Double Auction. */
  val auction = {

    val tradable = TestTradable()
    val askOrderMatchingRule = FindFirstAcceptableOrder[LimitAskOrder, LimitBidOrder with Persistent]()
    val bidOrderMatchingRule = FindFirstAcceptableOrder[LimitBidOrder, LimitAskOrder with Persistent]()
    KDoubleAuction(askOrderMatchingRule, bidOrderMatchingRule, 0.25, tradable)

  }

  val tradingRules = traders.map { trader =>

    // reservation value is private information
    val reservationValue = prng.nextDouble()

    if (prng.nextDouble() <= 0.5) {
      Left(new SellerEquilibriumTradingRule(buyerValuations, trader, auction.k, reservationValue, sellerValuations))
    } else {
      Right(new BuyerEquilibriumTradingRule(buyerValuations, trader, auction.k, reservationValue, sellerValuations))
    }

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


  /** Abstract base class for trading rules as defined in Satterthwaite and Williams (JET, 1989).
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param sellerValuations the distribution of seller valuations.
    * @tparam O a type of `Order with LimitPrice`.
    * @note both the buyer and seller valuation distributions should be defined on the unit interval.
    */
  protected[auctions] abstract class EquilibriumTradingRule[+O <: Order with LimitPrice](buyerValuations: distribution.RealDistribution,
                                                                                         sellerValuations: distribution.RealDistribution)
    extends ((Tradable) => O) {

    /* All theoretical results in the paper were derived for valuation distributions defined on [0, 1]! */
    require(buyerValuations.getSupportLowerBound == 0.0 && buyerValuations.getSupportUpperBound == 1.0)
    require(sellerValuations.getSupportLowerBound == 0.0 && sellerValuations.getSupportUpperBound == 1.0)

    protected def buyerInverseHazardRate(reservationValue: Double): Double = {
      (buyerValuations.cumulativeProbability(reservationValue) - 1) / buyerValuations.density(reservationValue)
    }

    protected def sellerInverseHazardRate(reservationValue: Double): Double = {
      sellerValuations.cumulativeProbability(reservationValue) / sellerValuations.density(reservationValue)
    }

    /** Virtual reservation values (terminology due to Myerson (Econometrica, 1984). */
    protected def virtualReservationValue(inverseHazardRate: (Double) => Double, reservationValue: Double): Double = {
      reservationValue + inverseHazardRate(reservationValue)
    }

  }


  /** Class implementing an equilibrium trading strategy for a buyer participating in a k-Double Auction.
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param issuer the `UUID` of the trader submitting the `LimitBidOrder`.
    * @param reservationValue the private reservation value for the `issuer`.
    * @param sellerValuations the distribution of seller valuations.
    */
  protected[auctions] class BuyerEquilibriumTradingRule(buyerValuations: distribution.UniformRealDistribution,
                                                        issuer: UUID,
                                                        k: Double,
                                                        reservationValue: Double,
                                                        sellerValuations: distribution.UniformRealDistribution)
    extends EquilibriumTradingRule[PersistentLimitBidOrder](buyerValuations, sellerValuations) {

    def apply(tradable: Tradable): PersistentLimitBidOrder = {
      val limit = Price((reservationValue + c * k) / (1 + k))
      PersistentLimitBidOrder(issuer, limit, 1, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

    private[this] val c = 0.5 * (1 - k)

    private[this] val d = 2 * (1 - c) / (2 * c + 1)

  }


  /** Class implementing an equilibrium trading strategy for a seller participating in a k-Double Auction.
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param issuer the `UUID` of the trader submitting the `LimitAskOrder`.
    * @param reservationValue the private reservation value for the `issuer`.
    * @param sellerValuations the distribution of seller valuations.
    */
  protected[auctions] class SellerEquilibriumTradingRule(buyerValuations: distribution.UniformRealDistribution,
                                                         issuer: UUID,
                                                         k: Double,
                                                         reservationValue: Double,
                                                         sellerValuations: distribution.UniformRealDistribution)
    extends EquilibriumTradingRule[PersistentLimitAskOrder](buyerValuations, sellerValuations) {

    def apply(tradable: Tradable): PersistentLimitAskOrder = {
      val limit = Price(c + (d * reservationValue) / (1 + k))
      PersistentLimitAskOrder(issuer, limit, 1, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

    private[this] val c = 0.5 * (1 - k)

    private[this] val d = 2 * (1 - c) / (2 * c + 1)

  }

}
