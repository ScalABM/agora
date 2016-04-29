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
package markets.orders.orderings.ask

import akka.actor.ActorSystem
import akka.testkit.TestKit

import java.util.UUID

import markets.orders.AskOrder
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketAskOrder
import markets.tradables.Tradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random


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

  val testTradable: Tradable = Tradable("AAPL")

  def uuid: UUID = {
    UUID.randomUUID()
  }

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
        randomLong(prng, lower, upper), testTradable, uuid)
      val lowPriceOrder = LimitAskOrder(testActor, lowPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      var orderBook = immutable.Seq[AskOrder](highPriceOrder, lowPriceOrder)

      When("an ask order arrives with a sufficiently low price, then this order should move to " +
        "the head of the book.")

      // initial state of the order book
      var expectedOrderBook = Seq[AskOrder](lowPriceOrder, highPriceOrder)
      orderBook.sorted(AskPriceTimeOrdering) should equal(expectedOrderBook)

      // simulate the arrival of a sufficiently low price order
      val lowestPriceOrder = MarketAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      orderBook = orderBook :+ lowestPriceOrder
      expectedOrderBook = Seq(lowestPriceOrder, lowPriceOrder, highPriceOrder)
      orderBook.sorted(AskPriceTimeOrdering) should equal(expectedOrderBook)

      When("an ask order arrives with a sufficiently high price, then this order should move to " +
        "the tail of the book.")

      // simulate arrival of a sufficiently high price order
      val highestPrice = randomLong(prng, highPrice, upper)
      val highestPriceOrder = LimitAskOrder(testActor, highestPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      orderBook = orderBook :+ highestPriceOrder
      expectedOrderBook = Seq(lowestPriceOrder, lowPriceOrder, highPriceOrder, highestPriceOrder)
      orderBook.sorted(AskPriceTimeOrdering) should equal(expectedOrderBook)

      When("an order arrives with the same price as another order already on the book, then " +
        "preference is given to the order with the earlier timestamp.")

      // simulate arrival of order with same price
      val samePrice = highPrice
      val earlierTime = randomLong(prng, lower, highPriceOrder.timestamp)
      val earlierOrder = LimitAskOrder(testActor, samePrice, randomLong(prng, lower, upper),
        earlierTime, testTradable, uuid)
      orderBook = orderBook :+ earlierOrder
      expectedOrderBook = Seq(lowestPriceOrder, lowPriceOrder, earlierOrder, highPriceOrder,
        highestPriceOrder)
      orderBook.sorted(AskPriceTimeOrdering) should equal(expectedOrderBook)

      When("an order arrives with the same price and timestamp as another order already on the" +
        " book, then preference is given to the existing order.")

      // simulate arrival of order with same price and timestamp
      val sameTime = highPriceOrder.timestamp
      val sameOrder = LimitAskOrder(testActor, samePrice, randomLong(prng, lower, upper),
        sameTime, testTradable, uuid)
      orderBook = orderBook :+ sameOrder
      expectedOrderBook = Seq(lowestPriceOrder, lowPriceOrder, earlierOrder, highPriceOrder,
        sameOrder, highestPriceOrder)
      orderBook.sorted(AskPriceTimeOrdering) should equal(expectedOrderBook)

    }
  }
}
