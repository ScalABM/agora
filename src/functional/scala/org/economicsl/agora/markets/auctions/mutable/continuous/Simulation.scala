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


import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.stat.descriptive.SummaryStatistics

import scala.util.Random

/*
trait Simulation extends App {

  /** Container for storing auction observables.
    *
    * @note eventually this might be a database connection. How to determine which data needs to be stored in-memory
    *       and which data needs to be written to disk?  How can we all the user to specify this?
    */
  def summaryStatistics: SummaryStatistics

  /** Create a single source of randomness for simulation in order to minimize indeterminacy
    *
    * @note this generator can be used to generate seeds for other random number generators.
    */
  def prng: RandomGenerator

  /** Specify a population of trading rules.
    *
    * @note
    */
  def tradingRules: Iterable[Either[SellerEquilibriumTradingRule, BuyerEquilibriumTradingRule]]

  /** Define an auction mechanism to simulate. */
  def auction: KDoubleAuction

  // simple for loop that actually runs a simulation...
  def simulate(T: Int): Unit = {

    for { t <- 0 until T} {

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

  }

}
*/