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

import akka.actor.{ActorRef, ActorSystem}
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
  
  feature("A OrderedBook with PriceOrdering should maintain price priority") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new order lands in SortedAskOrderBook with existing orders.") {

      Given("An ask order book that contains only existing limit orders")

      val highAskPrice = randomLong(prng, lower, upper)
      val lowAskPrice = randomLong(prng, lower, highAskPrice)
      val existingAsk1 = LimitAskOrder(testActor, highAskPrice, randomLong(prng, lower, 
        upper), randomLong(prng, lower, upper), testTradable)
      val existingAsk2 = LimitAskOrder(testActor, lowAskPrice, randomLong(prng, lower, 
        upper), randomLong(prng, lower, upper), testTradable)
      val askOrderBook = mutable.TreeSet[AskOrderLike]()(AskPriceOrdering())

      askOrderBook +=(existingAsk1, existingAsk2)

      When("additional limit orders arrive, the order book should maintain price priority.")

      // initial head of the order book
      askOrderBook.head should be(existingAsk2)

      // incoming order with lower price should move to the head of the book
      val aggressiveAskPrice = randomLong(prng, lower, lowAskPrice)
      val incomingAsk1 = LimitAskOrder(testActor, aggressiveAskPrice, randomLong(prng,
        lower, upper), randomLong(prng, lower, upper), testTradable)
      askOrderBook += incomingAsk1
      askOrderBook.headOption should be(Some(incomingAsk1))

      // generic incoming order should not change head of the book
      val passiveAskPrice = randomLong(prng, aggressiveAskPrice, upper)
      val incomingAsk2 = LimitAskOrder(testActor, passiveAskPrice, randomLong(prng, lower,
        upper), randomLong(prng, lower, upper), testTradable)
      askOrderBook += incomingAsk2
      askOrderBook.headOption should be(Some(incomingAsk1))

    }

    scenario("A new order lands in SortedBidOrderBook with existing orders.") {

      Given("An bid order book that contains existing orders")

      val highBidPrice = randomLong(prng, lower, upper)
      val lowBidPrice = randomLong(prng, lower, highBidPrice)
      val existingBid1 = LimitBidOrder(testActor, highBidPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val existingBid2 = LimitBidOrder(testActor, lowBidPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      val bidOrderBook = mutable.TreeSet[BidOrderLike]()(BidPriceOrdering())

      bidOrderBook +=(existingBid1, existingBid2)

      When("additional limit orders arrive, the order book should maintain price priority.")

      // initial head of the order book
      bidOrderBook.headOption should be(Some(existingBid1))

      // incoming order with higher price should move to the head of the book
      val aggressiveBidPrice = randomLong(prng, highBidPrice, upper)
      val incomingBid1 = LimitBidOrder(testActor, aggressiveBidPrice, randomLong(prng, lower,
        upper), randomLong(prng, lower, upper), testTradable)
      bidOrderBook += incomingBid1
      bidOrderBook.headOption should be(Some(incomingBid1))

      // generic incoming order should not change head of the book
      val passiveBidPrice = randomLong(prng, lower, aggressiveBidPrice)
      val incomingBid2 = LimitBidOrder(testActor, passiveBidPrice, randomLong(prng, lower, upper),
        randomLong(prng, lower, upper), testTradable)
      bidOrderBook += incomingBid2
      bidOrderBook.headOption should be(Some(incomingBid1))

    }
  }
}
