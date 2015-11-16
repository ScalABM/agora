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
package markets.orders.orderings

import scala.util.Random

import akka.actor.ActorSystem
import akka.testkit.TestKit
import markets.orders._
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}
import scala.collection.mutable


class TimeOrderingSpec extends TestKit(ActorSystem("TimeOrderingSpec")) with
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

  feature("An order book using TimeOrdering should sort orders low to high on timeStamp.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new order lands in an order book with existing orders.") {

      Given("An order book that contains existing orders")

      val lateTime = randomLong(prng, lower, upper)
      val earlyTime = randomLong(prng, lower, lateTime)
      val lateAskOrder = MarketAskOrder(testActor, randomLong(prng, lower, upper), lateTime,
        testTradable)
      val earlyAskOrder = LimitAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), earlyTime, testTradable)
      val askOrderBook = mutable.TreeSet[AskOrderLike]()(AskTimeOrdering())

      askOrderBook +=(lateAskOrder, earlyAskOrder)

      When("an aggressive limit order arrives, this order should move to the head of the book.")

      // initial head of the order book
      askOrderBook.toSeq should equal(Seq(earlyAskOrder, lateAskOrder))

      // incoming order with lower price should move to the head of the book
      val earlierTime = randomLong(prng, lower, earlyTime)
      val earlierAskOrder = LimitAskOrder(testActor, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), earlierTime, testTradable)
      askOrderBook += earlierAskOrder
      askOrderBook.toSeq should equal(Seq(earlierAskOrder, earlyAskOrder, lateAskOrder))

    }
  }
}
