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

import akka.actor.ActorSystem
import akka.testkit.TestKit

import java.util.UUID

import markets.orders.AskOrder
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketAskOrder
import markets.tradables.Security
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random


class AskTimeOrderingSpec extends TestKit(ActorSystem("AskTimeOrderingSpec")) with
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

  val testTradable: Security = Security("AAPL")

  def uuid: UUID = {
    UUID.randomUUID()  
  }
  
  feature("An order book using TimeOrdering should sort orders low to high on timeStamp.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new order lands in an order book with existing orders.") {

      Given("An order book that contains existing orders")

      val lateTime = randomLong(prng, lower, upper)
      val earlyTime = randomLong(prng, lower, lateTime)
      val lateOrder = LimitAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), lateTime, testTradable, uuid)
      val earlyOrder = LimitAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), earlyTime, testTradable, uuid)
      var orderBook = immutable.Seq[AskOrder](lateOrder, earlyOrder)

      When("an order arrives with a sufficiently early timestamp, then this order should move to " +
        "the head of the book.")

      // initial state of the order book
      var expectedOrderBook = Seq[AskOrder](earlyOrder, lateOrder)
      orderBook.sorted(AskTimeOrdering) should equal(expectedOrderBook)

      // simulate the arrival of a sufficiently early order
      val earlierTime = randomLong(prng, lower, earlyTime)
      val earlierOrder = MarketAskOrder(testActor, randomLong(prng, lower, upper), earlierTime,
        testTradable, uuid)
      orderBook = orderBook :+ earlierOrder
      expectedOrderBook = Seq(earlierOrder, earlyOrder, lateOrder)
      orderBook.sorted(AskTimeOrdering) should equal(expectedOrderBook)

      When("an order arrives with a sufficiently late timestamp, then this order should move to " +
        "the tail of the book.")

      // simulate arrival of a sufficiently late order
      val laterTime = randomLong(prng, lateTime, upper)
      val laterOrder = MarketAskOrder(testActor, randomLong(prng, lower, upper), laterTime,
        testTradable, uuid)
      orderBook = orderBook :+ laterOrder
      expectedOrderBook = Seq(earlierOrder, earlyOrder, lateOrder, laterOrder)
      orderBook.sorted(AskTimeOrdering) should equal(expectedOrderBook)

      When("an order arrives with the same timestamp as another order already on the book, then " +
        "preference is given to the existing order.")

      // simulate "simultaneous arrival" of orders
      val sameTime = lateTime
      val sameTimeOrder = LimitAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), sameTime, testTradable, uuid)
      orderBook = orderBook :+ sameTimeOrder
      expectedOrderBook = Seq(earlierOrder, earlyOrder, lateOrder, sameTimeOrder, laterOrder)
      orderBook.sorted(AskTimeOrdering) should equal(expectedOrderBook)

    }
  }
}
