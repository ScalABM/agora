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
package markets.auctions

import markets.matching.FindFirstMatchingFunction
import markets.pricing.AveragePricingFunction
import markets.tradables.Security
import markets.tradables.orders.ask.{AskOrder, LimitAskOrder}
import markets.tradables.orders.bid.{BidOrder, LimitBidOrder}
import org.scalameter.api._

import scala.util.Random


/** Performance tests for the `ContinuousDoubleAuction`. */
object ContinuousDoubleAuctionMicroBenchmark extends Bench.OnlineRegressionReport {

  import markets.RandomOrderGenerator._

  /* Setup the matching engine... */
  val tradable = Security(uuid())

  // These functions are stateless!
  val buyerPricingFunction = new AveragePricingFunction[BidOrder, AskOrder](0.5)
  val buyerMatchingFunction = new FindFirstMatchingFunction[BidOrder, AskOrder]

  val sellerPricingFunction = new AveragePricingFunction[AskOrder, BidOrder](0.5)
  val sellerMatchingFunction = new FindFirstMatchingFunction[AskOrder, BidOrder]

  /* Generate a range of numbers of orders to use when generating input data. */
  val numbersOfOrders = Gen.exponential("Number of Orders")(factor=2, until=math.pow(2, 11).toInt, from=2)

  /* Generate a streams of random orders using the different sizes... */
  val prng = new Random(42)
  val inputData = for { number <- numbersOfOrders } yield {

    // Auctions have mutable state!
    val buyerPostedPriceAuction = TestBuyerPostedPriceAuction(buyerMatchingFunction, buyerPricingFunction, tradable)
    val sellerPostedPriceAuction = TestSellerPostedPriceAuction(sellerMatchingFunction, sellerPricingFunction, tradable)
    val doubleAuction = TestContinuousDoubleAuction(buyerPostedPriceAuction, sellerPostedPriceAuction)

    val orders = for (i <- 1 to number) yield randomLimitOrder()

    (doubleAuction, orders)

  }

  def randomLimitOrder(askOrderProbability: Double=0.5): Either[LimitAskOrder, LimitBidOrder] = {
    if (prng.nextDouble() < askOrderProbability) {
      Left(randomLimitAskOrder(prng, tradable=tradable))
    } else {
      Right(randomLimitBidOrder(prng, tradable=tradable))
    }
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
