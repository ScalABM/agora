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
package org.economicsl.agora.markets.auctions.mutable.periodic

import java.util.UUID


import org.economicsl.agora.markets.tradables.orders.ask.PersistentLimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.PersistentLimitBidOrder
import org.economicsl.agora.markets.tradables.orders.Order
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, TestTradable, Tradable}

import scala.util.Random

import com.typesafe.config.ConfigFactory
import org.apache.commons.math3.{distribution, stat}


/** Simulation of the periodic k-Double Auction mechanism */
object KDoubleAuctionSimulation extends App {

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
    val k = config.getDouble("k")
    new KDoubleAuction(k, tradable)

  }

  val tradingRules = traders.map { trader =>

    if (prng.nextDouble() <= config.getDouble("ask-order-probability")) {
      Left(new SellerTradingRule(2.0, trader, buyerValuations.sample()))
    } else {
      Right(new BuyerTradingRule(0.5, trader, sellerValuations.sample()))
    }

  }

  // simple for loop that actually runs a simulation...
  for { t <- 0 until config.getInt("simulation-length")} {

    tradingRules.foreach {
      case Left(sellerTradingRule) =>
        val askOrder = sellerTradingRule(auction.tradable)
        auction.place(askOrder)
      case Right(buyerTradingRule) =>
        val bidOrder = buyerTradingRule(auction.tradable)
        auction.place(bidOrder)
    }

    auction.fill() match {
      case Some(fills) => fills.foreach(fill => summaryStatistics.addValue(fill.price.value))
      case None => None
    }

    auction.clear()

    println(s"Done with $t steps...")

  }

  // ...print to screen for reference...
  println(summaryStatistics.toString)


  protected[auctions] abstract class TradingRule[+O <: Order with LimitPrice](gamma: Double, issuer:UUID, reservationValue: Double)
    extends ((Tradable) => O)


  protected[auctions] class BuyerTradingRule(gamma: Double, issuer:UUID, reservationValue: Double)
    extends TradingRule[PersistentLimitBidOrder](gamma, issuer, reservationValue) {

    require(0.0 <= gamma && gamma <= 1.0, "Buyer must always bid weakly less than the reservation value.")

    def apply(tradable: Tradable): PersistentLimitBidOrder = {
      val limit = Price(gamma * reservationValue)
      PersistentLimitBidOrder(issuer, limit, 1, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

  }


  protected[auctions] class SellerTradingRule(gamma: Double, issuer: UUID, reservationValue: Double)
    extends TradingRule[PersistentLimitAskOrder](gamma, issuer, reservationValue) {

    require(1.0 <= gamma, "Seller must always ask weakly more than the reservation value.")

    def apply(tradable: Tradable): PersistentLimitAskOrder = {
      val limit = Price(gamma * reservationValue)
      PersistentLimitAskOrder(issuer, limit, 1, System.currentTimeMillis(), tradable, UUID.randomUUID())
    }

  }

}
