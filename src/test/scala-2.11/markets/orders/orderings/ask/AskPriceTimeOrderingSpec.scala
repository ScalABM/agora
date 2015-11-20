/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.orders.orderings.ask

import markets.orders._
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketAskOrder
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random

import akka.actor.ActorSystem
import akka.testkit.TestKit


class AskPriceTimeOrderingSpec extends TestKit(ActorSystem("AskPriceTimeOrderingSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers with
  BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  def randomLong(prng: Random, lower: Long, upper: Long): Long = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }

  val testTradable: TestTradable = TestTradable("AAPL")

  feature("An ask order book using AskPriceTimeOrdering should sort orders low to high on price. " +
    "If two orders have the same price, then orders are sorted low to high using timestamp.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new order lands in an ask order book with existing orders.") {

      Given("An ask order book that contains existing orders")

      val highPrice = randomLong(prng, lower, upper)
      val lowPrice = randomLong(prng, lower, highPrice)
      val highPriceOrder = LimitAskOrder(testActor, highPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val lowPriceOrder = LimitAskOrder(testActor, lowPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      var orderBook = immutable.TreeSet[AskOrderLike]()(AskPriceTimeOrdering)

      orderBook = orderBook + (highPriceOrder, lowPriceOrder)

      When("an ask order arrives with a sufficiently low price, then this order should move to " +
        "the head of the book.")

      // initial state of the order book
      orderBook.toSeq should equal(Seq(lowPriceOrder, highPriceOrder))

      // simulate the arrival of a sufficiently low price order
      val lowestPriceOrder = MarketAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      orderBook = orderBook + lowestPriceOrder
      orderBook.toSeq should equal(Seq(lowestPriceOrder, lowPriceOrder, highPriceOrder))

      When("an ask order arrives with a sufficiently high price, then this order should move to " +
        "the tail of the book.")

      // simulate arrival of a sufficiently high price order
      val highestPrice = randomLong(prng, highPrice, upper)
      val highestPriceOrder = LimitAskOrder(testActor, highestPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      orderBook = orderBook + highestPriceOrder
      orderBook.toSeq should equal(Seq(lowestPriceOrder, lowPriceOrder, highPriceOrder,
        highestPriceOrder))

      When("an order arrives with the same price as another order already on the book, then " +
        "preference is given to the order with the earlier timestamp.")

      // simulate arrival of order with same price
      val samePrice = highPrice
      val earlierTime = randomLong(prng, lower, highPriceOrder.timestamp)
      val earlierOrder = LimitAskOrder(testActor, samePrice, randomLong(prng, lower, upper),
        earlierTime, testTradable)
      orderBook = orderBook + earlierOrder
      orderBook.toSeq should equal(Seq(lowestPriceOrder, lowPriceOrder, earlierOrder,
        highPriceOrder, highestPriceOrder))

      When("an order arrives with the same price and timestamp as another order already on the" +
        " book, then preference is given to the existing order.")

      // simulate arrival of order with same price and timestamp
      val sameTime = highPriceOrder.timestamp
      val sameOrder = LimitAskOrder(testActor, samePrice, randomLong(prng, lower, upper),
        sameTime, testTradable)
      orderBook = orderBook + sameOrder
      orderBook.toSeq should equal(Seq(lowestPriceOrder, lowPriceOrder, earlierOrder,
        highPriceOrder, sameOrder, highestPriceOrder))

    }
  }
}
