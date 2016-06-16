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
package markets.engines

import markets.RandomOrderGenerator
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.tradables.Tradable
import org.scalameter.Bench
import org.scalameter.api.Gen

import scala.util.Random


/** Regression test suite for MutableTreeSetCDAMatchingEngine. */
object CDAMatchingEngineMicroBenchmark extends Bench.OnlineRegressionReport {

  import RandomOrderGenerator._

  /* Setup the matching engine... */
  val askOrdering = AskPriceTimeOrdering
  val bidOrdering = BidPriceTimeOrdering
  val initialPrice = 1
  val tradable = Tradable("XOM")
  val matchingEngine = CDAMatchingEngine(initialPrice, tradable)(askOrdering, bidOrdering)

  /* Generate a range of numbers of orders to use when generating input data. */
  val numbersOfOrders = Gen.exponential("Number of Orders")(10, 10, 1000000)

  /* Generate a streams of random orders using the different sizes... */
  val prng = new Random(42)
  val inputData = for { number <- numbersOfOrders } yield {
    Stream.fill(number)(randomOrder(prng, tradable = tradable))
  }

  performance of "CDAMatchingEngine" in {
    measure method "fill" in {
      using(inputData) in {
        orders => orders.map(matchingEngine.fill)
      }
    }
  }

 }
