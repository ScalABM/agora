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

  def randomLong(prng: Random, lower: Long = 1, upper: Long = Long.MaxValue): Long = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }

  val testTradable: TestTradable = TestTradable("AAPL")


  feature("A ContinuousDoubleAuction matching engine should be able to generate filled orders") {

    val prng: Random = new Random()

    scenario("A new ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      When("a LimitAskOrder arrives...")
      val askOrder = LimitAskOrder(testActor, randomLong(prng), randomLong(prng), randomLong(prng),
        testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(askOrder)

      Then("it should land in the ask order book")
      filledOrders should be(None)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(askOrder))

    }

    scenario("A new bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      When("a LimitBidOrder arrives...")
      val bidOrder = LimitBidOrder(testActor, randomLong(prng), randomLong(prng), randomLong(prng),
        testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(bidOrder)

      Then("it should land in the bid order book")

      filledOrders should be(None)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(bidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with the same quantity.") {

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val quantity = randomLong(prng)
      val bidOrder = LimitBidOrder(testActor, bidPrice, quantity, randomLong(prng), testTradable)
      matchingEngine.fillIncomingOrder(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper=bidPrice)
      val askOrder = LimitAskOrder(testActor, askPrice, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(askOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder((askOrder.issuer, bidOrder.issuer), bidPrice, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrderLike](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

    }

    scenario("A limit ask order crosses an existing limit bid order with a greater quantity.") {

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val bidOrder = LimitBidOrder(testActor, bidPrice, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fillIncomingOrder(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper=bidPrice)
      val askQuantity = randomLong(prng, upper=bidQuantity)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(askOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder((askOrder.issuer, bidOrder.issuer), bidPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrderLike](filledOrder)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(bidOrder.split(residualBidQuantity)))

    }

    scenario("A limit ask order crosses an existing limit bid order with a lesser quantity.") {

      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val bidOrder = LimitBidOrder(testActor, bidPrice, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fillIncomingOrder(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper=bidPrice)
      val askQuantity = randomLong(prng, lower=bidQuantity)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(askOrder)

      Then("the matching engine should generate a PartialFilledOrder")

      val filledOrder = PartialFilledOrder((askOrder.issuer, bidOrder.issuer), bidPrice,
        bidQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrderLike](filledOrder)))

      // also need to check that residual ask order landed in the book
      val residualAskQuantity = askQuantity - bidQuantity
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(askOrder.split(residualAskQuantity)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.isEmpty should be(true)
    }

    scenario("A limit bid order crosses an existing limit ask order with the same quantity.") {

      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val quantity = randomLong(prng)
      val askOrder = LimitAskOrder(testActor, askPrice, quantity, randomLong(prng), testTradable)
      matchingEngine.fillIncomingOrder(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLong(prng, lower=askPrice)
      val bidOrder = LimitBidOrder(testActor, bidPrice, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(bidOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder((askOrder.issuer, bidOrder.issuer), askPrice, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrderLike](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

    }

    scenario("A limit bid order crosses an existing limit ask order with a greater quantity.") {

      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val askQuantity = randomLong(prng)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, randomLong(prng), testTradable)
      matchingEngine.fillIncomingOrder(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLong(prng, lower=askPrice)
      val bidQuantity = randomLong(prng, upper=askQuantity)
      val bidOrder = LimitBidOrder(testActor, bidPrice, bidQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(bidOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder((bidOrder.issuer, askOrder.issuer), askPrice,
        bidQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrderLike](filledOrder)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.isEmpty should be(true)

      // also need to check that residual ask order landed in the book
      val residualAskQuantity = askQuantity - bidQuantity
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(askOrder.split(residualAskQuantity)))

    }

    scenario("A limit bid order crosses an existing limit ask order with a lesser quantity.") {

      val bidOrderBook = immutable.TreeSet.empty[BidOrderLike](BidPriceTimeOrdering)
      val askOrderBook = immutable.TreeSet.empty[AskOrderLike](AskPriceTimeOrdering)
      val matchingEngine = new ContinuousDoubleAuction(askOrderBook, bidOrderBook)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val askQuantity = randomLong(prng)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, randomLong(prng), testTradable)
      matchingEngine.fillIncomingOrder(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLong(prng, lower=askPrice)
      val bidQuantity = randomLong(prng, lower=askQuantity)
      val bidOrder = LimitBidOrder(testActor, bidPrice, bidQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fillIncomingOrder(bidOrder)

      Then("the matching engine should generate a PartialFilledOrder")

      val filledOrder = PartialFilledOrder((bidOrder.issuer, askOrder.issuer), askPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrderLike](filledOrder)))

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(bidOrder.split(residualBidQuantity)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
    }
  
  }

}
