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
package markets.clearing.engines

import markets.orders._
import markets.orders.orderings.{BidPriceTimeOrdering, AskPriceTimeOrdering}
import markets.tradables.TestTradable
import org.scalatest.{FeatureSpecLike, Matchers, BeforeAndAfterAll, GivenWhenThen}

import scala.collection.immutable
import scala.util.Random

import akka.actor.ActorSystem
import akka.testkit.TestKit


class ContinuousDoubleAuctionLikeSpec extends TestKit(ActorSystem("ContinuousDoubleAuctionLikeSpec")) with
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


  feature("A ContinuousDoubleAuction matching engine should be able to generate filled orders") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("A new ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      When("a LimitAskOrder arrives...")
      val askOrder = LimitAskOrder(testActor, 100, 50, 1, testTradable)
      matchingEngine.fillIncomingOrder(askOrder)

      Then("it should land in the ask order book")

      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(askOrder))

    }

    scenario("A new bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      When("a LimitBidOrder arrives...")
      val bidOrder = LimitBidOrder(testActor, 100, 50, 1, testTradable)
      matchingEngine.fillIncomingOrder(bidOrder)

      Then("it should land in the bid order book")

      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(bidOrder))

    }
  }
}
