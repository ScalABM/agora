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

import org.economicsl.agora.markets.auctions.pricing.WeightedAveragePricing
import org.economicsl.agora.markets.tradables.{TestTradable, Tradable}
import org.economicsl.agora.RandomOrderGenerator

import org.apache.commons.math3.{distribution, random}
import org.scalameter.api._


/** Performance tests for the `ContinuousDoubleAuction`. */
object DoubleAuctionMicroBenchmark extends Bench.OnlineRegressionReport {

  // Define a source for randomly generated orders...
  val orderGenerator: RandomOrderGenerator = {

    val seed = 42
    val prng = new random.MersenneTwister(seed)

    // specify the sampling distribution for prices (could use different distributions for ask and bid prices...)
    val (minAskPrice, maxAskPrice) = (50, 200)
    val askPriceDistribution = new distribution.UniformRealDistribution(prng, minAskPrice, maxAskPrice)

    val (minBidPrice, maxBidPrice) = (1, 150)
    val bidPriceDistribution = new distribution.UniformRealDistribution(prng, minBidPrice, maxBidPrice)

    // specify the sampling distribution for quantities could use different distributions for ask and bid quantities...)
    val (minQuantity, maxQuantity) = (1, 1)
    val askQuantityDistribution = new distribution.UniformIntegerDistribution(prng, minQuantity, maxQuantity)
    val bidQuantityDistribution = new distribution.UniformIntegerDistribution(prng, minQuantity, maxQuantity)

    RandomOrderGenerator(prng, askPriceDistribution, askQuantityDistribution, bidPriceDistribution, bidQuantityDistribution)

  }

  /** Define an instance of a `TwoSidedPostedPriceAuction`. */
  def createAuctionFor(tradable: Tradable) = {

    val weight = 0.5
    val askOrderPricingRule = WeightedAveragePricing(weight)
    val bidOrderPricingRule = WeightedAveragePricing(weight)

    new DoubleAuction(askOrderPricingRule, bidOrderPricingRule, tradable)

  }

  /* Generate a range of numbers of orders to use when generating input data. */
  val numbersOfOrders = Gen.exponential("Number of Orders")(factor=2, until=math.pow(2, 10).toInt, from=2)

  /* Generate a streams of random orders using the different sizes... */
  val inputData = for { number <- numbersOfOrders } yield {

    val tradable = TestTradable()

    val auction = createAuctionFor(tradable)

    val orders = for (i <- 1 to number) yield orderGenerator.nextLimitOrder(0.5, tradable)

    (auction, orders)

  }

  performance of "ContinuousDoubleAuction" config (
    exec.benchRuns -> 200,
    exec.independentSamples -> 20,
    exec.jvmflags -> List("-Xmx2G")
    ) in {

    measure method "fill" in {
      using(inputData) in {
        case (doubleAuction, orders) => orders.flatMap {
          case Left(limitAskOrder) => doubleAuction.fill(limitAskOrder)
          case Right(limitBidOrder) => doubleAuction.fill(limitBidOrder)
        }
      }
    }
  }

}
