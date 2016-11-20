package org.economicsl.agora.markets.auctions.mutable.continuous

import java.util.UUID

import org.economicsl.agora.markets.tradables.{Price, TestTradable}

import org.apache.commons.math3.distribution
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random


class SellersAskDoubleAuctionTradingRulesSpec extends FlatSpec with Matchers {

  "Limit price generated by a seller's equilibrium trading rule" should "be average the reservation value and 1" in {

    val prng = new Random()

    val buyerValuations = new distribution.UniformRealDistribution()
    val issuer = UUID.randomUUID()
    val reservationValue = prng.nextDouble()
    val sellerValuations = new distribution.UniformRealDistribution()

    val tradingRule = {
      new SellersAskDoubleAuctionSimulation.SellerEquilibriumTradingRule(buyerValuations, issuer, reservationValue, sellerValuations)
    }

    val tradable = TestTradable()
    val expectedValue = 0.5 * (reservationValue + 1) // analytic result for uniform valuation distributions
    tradingRule(tradable).limit should be(Price(expectedValue))

  }

}