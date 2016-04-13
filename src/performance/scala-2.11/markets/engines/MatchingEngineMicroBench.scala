/*
Copyright 2016 David R. Pugh

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

import akka.testkit.TestKit

import com.typesafe.config.ConfigFactory
import markets.MarketsTestKit
import markets.engines.MatchingEngine
import markets.orders.Order
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.{TestTradable, Tradable}
import org.scalameter.Bench

import scala.util.Random


/** Base trait for regression testing a MatchingEngine. */
trait MatchingEngineMicroBench extends Bench.OnlineRegressionReport
  with MarketsTestKit {

  /** MatchingEngine to use when running the benchmark tests. */
  def matchingEngine: MatchingEngine

  /** TestKit used to generate testing actors. */
  def testKit: TestKit

  val config = ConfigFactory.load("matchingEnginesBenchmark.conf")

  val askOrderProbability = config.getDouble("matching-engines.input-data.askOrderProbability")

  val limitOrderProbability = config.getDouble("matching-engines.input-data.limitOrderProbability")

  val prng = new Random(config.getLong("matching-engines.input-data.seed"))

  val tradable = TestTradable(config.getString("matching-engines.input-data.symbol"))

  /** Generates a random limit order for some tradable.
    *
    * @param tradable
    * @return
    */
  protected def generateLimitOrder(tradable: Tradable): Order = {
    val sender = testKit.testActor
    val price = randomLimitPrice(prng)
    val quantity = randomQuantity(prng)
    if (askOrderProbability <= prng.nextDouble()) {
      LimitAskOrder(sender, price, quantity, timestamp(), tradable, uuid())
    } else {
      LimitBidOrder(sender, price, quantity, timestamp(), tradable, uuid())
    }
  }

  /** Generates a collection of random limit orders for some tradable.
    *
    * @param numberOrders
    * @param tradable
    * @return
    */
  protected def generateLimitOrders(numberOrders: Int, tradable: Tradable) = {
    for { i <- 1 to numberOrders } yield generateLimitOrder(tradable)
  }

  /** Generates a random market order for some tradable.
    *
    * @param tradable
    * @return
    */
  protected def generateMarketOrder(tradable: Tradable): Order = {
    val sender = testKit.testActor
    val quantity = randomQuantity(prng)
    if (askOrderProbability <= prng.nextDouble()) {
      MarketAskOrder(sender, quantity, timestamp(), tradable, uuid())
    } else {
      MarketBidOrder(sender, quantity, timestamp(), tradable, uuid())
    }
  }

  /** Generates a collection of random market orders for some tradable.
    *
    * @param numberOrders
    * @param tradable
    * @return
    */
  protected def generateMarketOrders(numberOrders: Int, tradable: Tradable) = {
    for { i <- 1 to numberOrders } yield generateMarketOrder(tradable)
  }

  /** Generates a random order for some tradable.
    *
    * @param tradable
    * @return
    */
  protected def generateOrder(tradable: Tradable): Order = {
    if (limitOrderProbability <= prng.nextDouble()) {
      generateLimitOrder(tradable)
    } else {
      generateMarketOrder(tradable)
    }
  }

  /** Generates a collection of random orders for some tradable.
    *
    * @param numberOrders
    * @param tradable
    * @return
    */
  protected def generateOrders(numberOrders: Int, tradable: Tradable) = {
    for { i <- 1 to numberOrders } yield generateOrder(tradable)
  }

}
