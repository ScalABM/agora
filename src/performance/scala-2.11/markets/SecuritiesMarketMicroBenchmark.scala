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
package markets

import markets.tradables.Tradable
import org.scalameter.api._

import scala.util.Random


/** Performance tests for the CDAMatchingEngine class. */
object SecuritiesMarketMicroBenchmark extends Bench.OnlineRegressionReport {

  import RandomOrderGenerator._

  /* Setup the matching engine... */
  val initialPrice = 1
  val tradable = Tradable("XOM")
  val matchingEngine = SecuritiesMarket(initialPrice, tradable)

  /* Generate a range of numbers of orders to use when generating input data. */
  val numbersOfOrders = Gen.exponential("Number of Orders")(factor=10, until=1000000, from=10)

  /* Generate a streams of random orders using the different sizes... */
  val prng = new Random(42)
  val inputData = for { number <- numbersOfOrders } yield {
    for (i <- 1 to number) yield randomOrder(prng, tradable = tradable)
  }

  performance of "CDAMatchingEngine" config (
    exec.benchRuns -> 200,
    exec.independentSamples -> 20,
    exec.jvmflags -> List("-Xmx2G")
    ) in {
    measure method "fill" in {
      using(inputData) in {
        orders => orders.map(matchingEngine.fill)
      }
    }
  }

}
