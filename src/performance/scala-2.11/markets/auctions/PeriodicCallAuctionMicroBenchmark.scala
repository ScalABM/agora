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

import markets.RandomOrderGenerator
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable
import org.scalameter.api._

import scala.util.Random


/** Performance tests for the PeriodicCallAuction class. */
object PeriodicCallAuctionMicroBenchmark extends Bench.OnlineRegressionReport {

  import RandomOrderGenerator._

  /* Setup the matching engine... */
  val prng = new Random(42)
  val initialPrice = 1e6 * prng.nextDouble()
  val tradable = Tradable("XOM")

  val sizes = Gen.exponential("Number of existing orders")(factor=10, until=1000000, from=1000)

  /** Generates a collection of auction mechanism with order books of increasing size. */
  val auctionMechanisms = for { size <- sizes } yield {
    val auctionMechanism = PeriodicCallAuction(initialPrice, tradable, maxEval = 500)
    val orders = for (i <- 1 to size) yield randomOrder(prng, tradable = tradable)
    orders.foreach{
      case order: AskOrder => auctionMechanism.askOrderBook.add(order)
      case order: BidOrder => auctionMechanism.bidOrderBook.add(order)
    }
    auctionMechanism
  }

  performance of "PeriodicCallAuction" config (
    exec.benchRuns -> 200,
    exec.independentSamples -> 20,
    exec.jvmflags -> List("-Xmx2G")
    ) in {

    measure method "fill" in {
      using(auctionMechanisms) in {
        auctionMechanism => auctionMechanism.fill()
      }
    }

    // don't want to measure time! Want to measure the value of the price!
    /*
    measure method "findMarketClearingPrice" in {

      using(auctionMechanisms) in {
        auctionMechanism => auctionMechanism.findMarketClearingPrice(maxEval = 500)
      }
    }
    */

  }

}
