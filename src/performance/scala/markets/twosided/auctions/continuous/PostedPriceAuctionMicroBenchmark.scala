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
package markets.twosided.auctions.continuous

import markets.tradables.orders.ask.LimitAskOrder
import markets.tradables.orders.bid.LimitBidOrder
import markets.tradables.TestTradable
import markets.twosided
import org.apache.commons.math3.{distribution, random}
import org.scalameter.api._


/** Performance tests for the `ContinuousDoubleAuction`. */
object PostedPriceAuctionMicroBenchmark extends Bench.OnlineRegressionReport {

  val seed = 42
  val prng = new random.MersenneTwister(seed)

  val orderGenerator: markets.RandomOrderGenerator = {

    // specify the sampling distribution for prices
    val (minPrice, maxPrice) = (1, 200)
    val priceDistribution = new distribution.UniformRealDistribution(prng, minPrice, maxPrice)

    // specify the sampling distribution for quantities
    val (minQuantity, maxQuantity) = (1, 1)
    val quantityDistribution = new distribution.UniformIntegerDistribution(prng, minQuantity, maxQuantity)

    markets.RandomOrderGenerator(prng, priceDistribution, quantityDistribution)

  }

  /* Setup the matching engine... */
  val tradable = TestTradable()

  // These functions are stateless!
  val matchingFunction = twosided.matching.BestPriceMatchingFunction[LimitAskOrder, LimitBidOrder]()
  val pricingFunction = twosided.pricing.AveragePricingFunction[LimitAskOrder, LimitBidOrder](weight=0.5)

  /* Generate a range of numbers of orders to use when generating input data. */
  val numbersOfOrders = Gen.exponential("Number of Orders")(factor=2, until=math.pow(2, 10).toInt, from=2)

  /* Generate a streams of random orders using the different sizes... */
  val inputData = for { number <- numbersOfOrders } yield {

    // Auctions have mutable state!
    val doubleAuction = TestPostedPriceAuction(matchingFunction, pricingFunction, tradable)

    val orders = for (i <- 1 to number) yield orderGenerator.nextLimitOrder(0.5, tradable)

    (doubleAuction, orders)

  }

  performance of "ContinuousDoubleAuction" config (
    exec.benchRuns -> 200,
    exec.independentSamples -> 20,
    exec.jvmflags -> List("-Xmx2G")
    ) in {

    measure method "fill" in {
      using(inputData) in {
        case (doubleAuction, orders) => orders.map {
          case Left(limitAskOrder) => doubleAuction.fill(limitAskOrder)
          case Right(limitBidOrder) => doubleAuction.fill(limitBidOrder)
        }
      }
    }
  }

}
