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

import org.economicsl.agora.markets.tradables._
import org.apache.commons.math3.{distribution, random, stat}
import com.typesafe.config.ConfigFactory
import org.apache.commons.math3.random.MersenneTwister
import org.economicsl.agora.markets.{Fill, RandomBuyer, RandomSeller}

import scala.util.Random


/** Simulation replicates results from the seminal Gode and Sunder (JPE, 1996) "Zero-Intelligence" agents paper. */
object GodeSunderSimulation extends App {

  val config = ConfigFactory.load("godeSunderSimulation.conf")

  // Create something to store simulated prices
  val summaryStatistics = new stat.descriptive.SummaryStatistics()
  val performanceDistribution = new random.EmpiricalDistribution()

  // Create a single source of randomness for simulation in order to minimize indeterminacy
  val seed = config.getLong("seed")
  val prng = new MersenneTwister(seed)

  // Create a collection of traders each with it own trading rule
  val numberTraders = config.getInt("number-traders")
  val traders = for { i <- 1 to numberTraders } yield UUID.randomUUID()

  // valuation distributions are assumed common knowledge
  val redemptionValues = new distribution.UniformRealDistribution(prng, 1, 200)
  val unitCosts = new distribution.UniformRealDistribution(prng, 1, 200)

  /** Define the a double auction mechanism. */
  val auction = {
    val tradable = TestTradable()
    GodeSunderDoubleAuction(tradable)
  }

  val tradingRules = traders.map { trader =>

    if (prng.nextDouble() <= config.getDouble("ask-order-probability")) {
      val unitCost = unitCosts.sample()
      Left(new ZeroIntelligenceSeller(trader, unitCost, unitCosts))
    } else {
      val redemptionValue = redemptionValues.sample()
      Right(new ZeroIntelligenceBuyer(trader, redemptionValue, redemptionValues))
    }

  }

  // simple for loop that actually runs a simulation...
  for { t <- 0 until config.getInt("simulation-length")} {

    Random.shuffle(tradingRules).foreach {
      case Left(sellerTradingRule) =>
        val askOrder = sellerTradingRule(auction.tradable)
        auction.fill(askOrder).foreach { fill =>
          summaryStatistics.addValue(fill.price.value); sellerTradingRule.observe(fill)
        }
      case Right(buyerTradingRule) =>
        val bidOrder = buyerTradingRule(auction.tradable)
        auction.fill(bidOrder).foreach {
          fill => summaryStatistics.addValue(fill.price.value); buyerTradingRule.observe(fill)
        }
    }

    auction.clear()

    println(s"Done with $t steps...")

  }

  // ...example of a cross sectional computation that is data parallel!
  val averagePerformance = tradingRules.map {
    case Left(sellerTradingRule) => sellerTradingRule.performanceSummary.getMean
    case Right(buyerTradingRule) => buyerTradingRule.performanceSummary.getMean
  }.filterNot ( performance => performance.isNaN )
  performanceDistribution.load(averagePerformance.toArray)

  // ...print to screen for reference...
  println(summaryStatistics.toString)
  println(performanceDistribution.getSampleStats.toString)


  protected[auctions] class ZeroIntelligenceBuyer(issuer: UUID,
                                                  redemptionValue: Double,
                                                  values: distribution.RealDistribution)
    extends RandomBuyer(issuer, values) {

    override def observe: PartialFunction[Any, Unit] = {
      case message: Fill => performanceSummary.addValue(redemptionValue - message.price.value)
    }

  }


  protected[auctions] class ZeroIntelligenceSeller(issuer: UUID,
                                                   unitCost: Double,
                                                   values: distribution.RealDistribution)
    extends RandomSeller(issuer, values) {

    override def observe: PartialFunction[Any, Unit] = {
      case message: Fill => performanceSummary.addValue(message.price.value - unitCost)
    }

  }

}
