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
package org.economicsl.agora.markets

import java.util.UUID

import org.economicsl.agora.markets.auctions.matching.FindFirstAcceptableOrder
import org.economicsl.agora.markets.auctions.mutable.continuous.KDoubleAuction
import org.economicsl.agora.markets.tradables.{Quantity, TestTradable}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder
import org.apache.commons.math3.{random, stat}


/** Example simulation designed to provide a concrete point of departure for ongoing design discussions. */
object ExampleSimulation extends App {

  // Create something to store generated fill prices todo should be a database connection!
  val summaryStatistics = new stat.descriptive.SummaryStatistics()

  // Create a single source of randomness for simulation in order to minimize indeterminacy
  val SEED = 42
  val PRNG = new random.MersenneTwister(SEED)

  // Create a collection of traders each with it own trading rule
  val numberTraders = 10
  val traders = for { i <- 1 to numberTraders } yield UUID.randomUUID()
  val tradingRules = traders.map { trader =>
    val seed = PRNG.nextLong()
    val prng = new random.MersenneTwister(seed)
    new UniformRandomTradingRule(0.5, trader, prng)
  }

  /** Define an auction mechanism
    *
    * This particular auction mechanism simulates random search until an acceptable order is found.
    */
  val auction = {

    val tradable = TestTradable()
    val askOrderMatchingRule = FindFirstAcceptableOrder[LimitAskOrder with Quantity, LimitBidOrder with Persistent with Quantity]()
    val bidOrderMatchingRule = FindFirstAcceptableOrder[LimitBidOrder with Quantity, LimitAskOrder with Persistent with Quantity]()
    val k = 0.5  // buyer and seller split the trading profit equally!
    KDoubleAuction(askOrderMatchingRule, bidOrderMatchingRule, k, tradable)

  }

  // simple for loop that actually runs a simulation...
  for { t <- 0 until 10000} {

    // ...generate a batch of orders...this step is trivially parallel!
    val orders = tradingRules.map(tradingRule => tradingRule(auction.tradable))

    // ...feed the orders into the auction mechanism...amount of parallelism in this step varies!
    val fills = orders.flatMap {
      case Left(limitAskOrder) => auction.fill(limitAskOrder)
      case Right(limitBidOrder) => auction.fill(limitBidOrder)
    }

    // ...collect the generated prices...this step is trivially parallel! todo this should involve writing to database
    fills.foreach(fill => summaryStatistics.addValue(fill.price.value))

  }

  // ...print to screen for reference...
  println(summaryStatistics.toString)

}
