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
package markets.orders.orderings.bid

import akka.actor.ActorSystem
import akka.testkit.TestKit

import java.util.UUID

import markets.orders.BidOrder
import markets.orders.limit.LimitBidOrder
import markets.orders.market.MarketBidOrder
import markets.tradables.Tradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random


class BidPriceOrderingSpec extends TestKit(ActorSystem("BidPriceOrderingSpec")) with
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
  
  feature("An bid order book using BidPriceOrdering should sort orders high to low on price.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new bid order lands in an bid order book with existing orders.") {

      Given("An order book that contains existing orders")

      val highPrice = randomLong(prng, lower, upper)
      val lowPrice = randomLong(prng, lower, highPrice)
      val highPriceOrder = LimitBidOrder(testActor, highPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      val lowPriceOrder = LimitBidOrder(testActor, lowPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      var orderBook = immutable.Seq[BidOrder](lowPriceOrder, highPriceOrder)

      When("an order arrives with a sufficiently low price, then this order should move to " +
        "the tail of the book.")

      // initial state of the order book
      var expectedOrderBook = Seq[BidOrder](highPriceOrder, lowPriceOrder)
      orderBook.sorted(new BidPriceOrdering) should equal(expectedOrderBook)

      // simulate the arrival of a sufficiently low price order
      val lowestPrice = randomLong(prng, lower, lowPrice)
      val lowestPriceOrder = LimitBidOrder(testActor, lowestPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      orderBook = orderBook :+ lowestPriceOrder
      expectedOrderBook = Seq(highPriceOrder, lowPriceOrder, lowestPriceOrder)
      orderBook.sorted(new BidPriceOrdering) should equal(expectedOrderBook)

      When("an order arrives with a sufficiently high price, then this order should move to " +
        "the head of the book.")

      // simulate arrival of a sufficiently high price order
      val highestPriceOrder = MarketBidOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      orderBook = orderBook :+ highestPriceOrder
      expectedOrderBook = Seq(highestPriceOrder, highPriceOrder, lowPriceOrder, lowestPriceOrder)
      orderBook.sorted(new BidPriceOrdering) should equal(expectedOrderBook)

      When("an order arrives with the same price as another order already on the book, then " +
        "preference is given to the existing order.")

      // simulate arrival of order with same price
      val samePrice = highPrice
      val samePriceOrder = LimitBidOrder(testActor, samePrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable, uuid)
      orderBook = orderBook :+ samePriceOrder
      expectedOrderBook = Seq(highestPriceOrder, highPriceOrder, samePriceOrder, lowPriceOrder,
        lowestPriceOrder)
      orderBook.sorted(new BidPriceOrdering) should equal(expectedOrderBook)

    }
  }
}
