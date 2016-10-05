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
package markets.matching

import markets.mutable.orderbooks.OrderBook
import markets.tradables.Security
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.LimitBidOrder
import org.scalameter.api._
import org.scalameter.{Bench, Gen}

import scala.util.Random


object FindFirstMatchingMicroBenchmark extends Bench.OnlineRegressionReport {

  import markets.RandomOrderGenerator._

  val prng = new Random(42)

  val validTradable = Security(uuid())

  val sizes = Gen.exponential("Number of orders")(factor=10, until=1000000, from=10)

  /** Generates a collection of `OrderBook` instances of increasing size. */
  val orderBooks = for { size <- sizes } yield {
    val orderBook = OrderBook[AskOrder](validTradable)
    val orders = for (i <- 1 to size) yield randomAskOrder(prng, tradable = validTradable)
    orders.foreach( order => orderBook.add(order) )
    orderBook
  }

  /** Use closure to inject orderBook into a matching function. */
  val matchingFunctions = for { orderBook <- orderBooks } yield {
    val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
    order: LimitBidOrder => matchingFunction(order, orderBook)
  }

  performance of "FindFirstMatchingFunction" config (
    //reports.resultDir -> "target/benchmarks/markets/engines/orderbooks/OrderBook",
    exec.benchRuns -> 200,
    exec.independentSamples -> 20,
    exec.jvmflags -> List("-Xmx2G")
    ) in {

    measure method "apply" in {
      using(matchingFunctions) in {
        matchingFunction =>
          val bidOrder = randomLimitBidOrder(prng, tradable=validTradable)
          matchingFunction(bidOrder)
      }
    }

  }

}
