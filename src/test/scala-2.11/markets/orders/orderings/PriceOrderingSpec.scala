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


class PriceOrderingSpec extends TestKit(ActorSystem("PriceOrderingSpec")) with
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

  feature("An order book using AskPriceOrdering should sort orders low to high on price.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new order lands in an order book with existing orders.") {

      Given("An order book that contains only existing limit orders")

      val highAskPrice = randomLong(prng, lower, upper)
      val lowAskPrice = randomLong(prng, lower, highAskPrice)
      val highAskOrder = LimitAskOrder(testActor, highAskPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val lowAskOrder = LimitAskOrder(testActor, lowAskPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val askOrderBook = mutable.TreeSet[AskOrderLike]()(AskPriceOrdering())

      askOrderBook +=(highAskOrder, lowAskOrder)

      When("an aggressive limit order arrives, this order should move to the head of the book.")

      // initial head of the order book
      askOrderBook.toSeq should equal(Seq(lowAskOrder, highAskOrder))

      // incoming order with lower price should move to the head of the book
      val aggressiveAskPrice = randomLong(prng, lower, lowAskPrice)
      val aggressiveAskOrder = LimitAskOrder(testActor, aggressiveAskPrice, randomLong(prng,
        lower, upper), randomLong(prng, lower, upper), testTradable)
      askOrderBook += aggressiveAskOrder
      askOrderBook.toSeq should equal(Seq(aggressiveAskOrder, lowAskOrder, highAskOrder))

    }

  }

  feature("An order book using BidPriceOrdering should sort orders high to low on price.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new order lands in an order book with existing orders.") {

      Given("An order book that contains existing orders")

      val highBidPrice = randomLong(prng, lower, upper)
      val lowBidPrice = randomLong(prng, lower, highBidPrice)
      val highBidOrder = LimitBidOrder(testActor, highBidPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val lowBidOrder = LimitBidOrder(testActor, lowBidPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val bidOrderBook = mutable.TreeSet[BidOrderLike]()(BidPriceOrdering())

      bidOrderBook +=(highBidOrder, lowBidOrder)

      When("an aggressive limit order arrives, this order should move to the head of the book.")

      // initial head of the order book
      bidOrderBook.toSeq should equal(Seq(highBidOrder, lowBidOrder))

      // incoming order with higher price should move to the head of the book
      val aggressiveBidPrice = randomLong(prng, highBidPrice, upper)
      val aggressiveBidOrder = LimitBidOrder(testActor, aggressiveBidPrice, randomLong(prng, lower,
        upper), randomLong(prng, lower, upper), testTradable)
      bidOrderBook += aggressiveBidOrder
      bidOrderBook.toSeq should equal(Seq(aggressiveBidOrder, highBidOrder, lowBidOrder))

    }
  }
}
