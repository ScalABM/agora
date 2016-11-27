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
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{ConcurrentAskOrderBook, ConcurrentBidOrderBook}
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{Quantity, TestTradable}
import com.typesafe.config.ConfigFactory
import org.apache.commons.math3.{distribution, stat}

import scala.util.Random


/** Multi-threaded simulation of the continuous k-Double Auction mechanism of Satterthwaite and Williams (JET, 1989). */
object ConcurrentKDoubleAuctionSimulation extends App {

  val config = ConfigFactory.load("kDoubleAuctionSimulation.conf")

  // Create something to store simulated prices
  val summaryStatistics = new stat.descriptive.SummaryStatistics()

  // Create a single source of randomness for simulation in order to minimize indeterminacy
  val seed = config.getLong("seed")
  val prng = new Random(seed)

  // Create a collection of traders each with it own trading rule
  val numberTraders = config.getInt("number-traders")
  val traders = for { i <- 1 to numberTraders } yield UUID.randomUUID()

  // valuation distributions are assumed common knowledge
  val buyerValuations = new distribution.UniformRealDistribution()
  val sellerValuations = new distribution.UniformRealDistribution()

  /** Define the a k-Double Auction. */
  val auction = {

    val tradable = TestTradable()
    val askOrderBook = ConcurrentAskOrderBook[LimitAskOrder with Persistent with Quantity](tradable)
    val askOrderMatchingRule = FindFirstAcceptableOrder[LimitAskOrder with Quantity, LimitBidOrder with Persistent with Quantity]()
    val bidOrderBook = ConcurrentBidOrderBook[LimitBidOrder with Persistent with Quantity](tradable)
    val bidOrderMatchingRule = FindFirstAcceptableOrder[LimitBidOrder with Quantity, LimitAskOrder with Persistent with Quantity]()
    val k = config.getDouble("k")
    KDoubleAuction(askOrderBook, askOrderMatchingRule, bidOrderBook, bidOrderMatchingRule, k, tradable)

  }

  /** Create a parallel collection of trading rules. */
  val tradingRules = traders.par.map { trader =>

    // reservation value is private information
    val reservationValue = prng.nextDouble()

    if (prng.nextDouble() <= config.getDouble("ask-order-probability")) {
      Left(new KDoubleAuctionSimulation.SellerEquilibriumTradingRule(buyerValuations, trader, auction.k, reservationValue, sellerValuations))
    } else {
      Right(new KDoubleAuctionSimulation.BuyerEquilibriumTradingRule(buyerValuations, trader, auction.k, reservationValue, sellerValuations))
    }

  }

  // simple for loop that actually runs a simulation...
  for { t <- 0 until config.getInt("simulation-length")} {

    tradingRules.foreach {
      case Left(sellerTradingRule) =>
        val askOrder = sellerTradingRule(auction.tradable)
        auction.fill(askOrder).foreach(fill => summaryStatistics.addValue(fill.price.value))
      case Right(buyerTradingRule) =>
        val bidOrder = buyerTradingRule(auction.tradable)
        auction.fill(bidOrder).foreach(fill => summaryStatistics.addValue(fill.price.value))
    }

    auction.clear()

    println(s"Done with $t steps...")

  }

  // ...print to screen for reference...
  println(summaryStatistics.toString)

}
