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

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestKit

import markets.orders.filled.{FilledOrder, PartialFilledOrder, TotalFilledOrder}
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random


class CDAMatchineEngineSpec extends TestKit(ActorSystem("CDAMatchineEngineSpec")) with
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

  val askOrderIssuer: ActorRef = testActor

  val bidOrderIssuer: ActorRef = testActor


  feature("A CDAMatchingEngine matching engine should be able to generate filled orders") {

    val prng: Random = new Random()

    scenario("A new limit ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a LimitAskOrder arrives...")
      val askOrder = LimitAskOrder(askOrderIssuer, randomLong(prng), randomLong(prng), randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("it should land in the ask order book")
      filledOrders should be(None)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(askOrder))

    }

    scenario("A new market ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a MarketAskOrder arrives...")
      val askOrder = MarketAskOrder(askOrderIssuer, randomLong(prng), randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("it should land in the ask order book.")
      filledOrders should be(None)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(askOrder))

    }

    scenario("A new limit bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a LimitBidOrder arrives...")
      val bidOrder = LimitBidOrder(bidOrderIssuer, randomLong(prng), randomLong(prng), randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("it should land in the bid order book.")

      filledOrders should be(None)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(bidOrder))

    }

    scenario("A new market bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a MarketBidOrder arrives...")
      val bidOrder = MarketBidOrder(bidOrderIssuer, randomLong(prng), randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("it should land in the bid order book.")

      filledOrders should be(None)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(bidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with the same quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val quantity = randomLong(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper = bidPrice)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a TotalFilledOrder at the bid price.")

      val filledOrder = TotalFilledOrder(askOrder.issuer, bidOrder.issuer, bidPrice, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

      //also should check that the reference price has been updated
      matchingEngine.mostRecentPrice should be(bidPrice)

    }

    scenario("A limit ask order crosses an existing market bid order with the same quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with only existing market bid order on its book...")
      val quantity = randomLong(prng)
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a TotalFilledOrder at the reference price.")

      val price = matchingEngine.mostRecentPrice
      val filledOrder = TotalFilledOrder(askOrder.issuer, bidOrder.issuer, price, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

    }

    scenario("A market ask order crosses an existing limit bid order with the same quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val quantity = randomLong(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper = bidPrice)
      val askOrder = MarketAskOrder(askOrderIssuer, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a TotalFilledOrder at the bid price.")

      val filledOrder = TotalFilledOrder(askOrder.issuer, bidOrder.issuer, bidPrice, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

    }

    scenario("A limit ask order crosses an existing limit bid order with a greater quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper = bidPrice)
      val askQuantity = randomLong(prng, upper = bidQuantity)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder(askOrder.issuer, bidOrder.issuer, bidPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      val residualBidOrder = bidOrder.split(residualBidQuantity)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(residualBidOrder))

    }

    scenario("A limit ask order crosses an existing market bid order with a greater quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing market and limit bid orders on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng),
        testTradable)
      matchingEngine.fill(limitBidOrder)

      val marketBidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(marketBidOrder)

      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(marketBidOrder, limitBidOrder))

      When("an incoming LimitAskOrder crosses the existing market bid order...")
      val askPrice = randomLong(prng, upper = bidPrice)
      val askQuantity = randomLong(prng, upper = bidQuantity)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a TotalFilledOrder at the best limit price.")

      val filledOrder = TotalFilledOrder(askOrder.issuer, marketBidOrder.issuer, bidPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      val residualBidOrder = marketBidOrder.split(residualBidQuantity)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(residualBidOrder, limitBidOrder))

    }

    scenario("A market ask order crosses an existing limit bid order with a greater quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askQuantity = randomLong(prng, upper = bidQuantity)
      val askOrder = MarketAskOrder(askOrderIssuer, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder(askOrder.issuer, bidOrder.issuer, bidPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      val residualBidOrder = bidOrder.split(residualBidQuantity)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(residualBidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with a lesser quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLong(prng, upper = bidPrice)
      val askQuantity = randomLong(prng, lower = bidQuantity)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a PartialFilledOrder")

      val filledOrder = PartialFilledOrder(askOrder.issuer, bidOrder.issuer, bidPrice,
        bidQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also need to check that residual ask order landed in the book
      val residualAskQuantity = askQuantity - bidQuantity
      val residualAskOrder = askOrder.split(residualAskQuantity)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(residualAskOrder))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.isEmpty should be(true)
    }

    scenario("A market ask order crosses an existing limit bid order with a lesser quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLong(prng)
      val bidQuantity = randomLong(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askQuantity = randomLong(prng, lower = bidQuantity)
      val askOrder = MarketAskOrder(askOrderIssuer, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a PartialFilledOrder")

      val filledOrder = PartialFilledOrder(askOrder.issuer, bidOrder.issuer, bidPrice,
        bidQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also need to check that residual ask order landed in the book
      val residualAskQuantity = askQuantity - bidQuantity
      val residualAskOrder = askOrder.split(residualAskQuantity)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(residualAskOrder))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.isEmpty should be(true)
    }


    scenario("A limit bid order crosses an existing limit ask order with the same quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val quantity = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, quantity, randomLong(prng), testTradable)
      matchingEngine.fill(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLong(prng, lower = askPrice)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder(bidOrder.issuer, askOrder.issuer, askPrice, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

    }

    scenario("A market bid order crosses an existing limit ask order with the same quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val quantity = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, quantity, randomLong(prng), testTradable)
      matchingEngine.fill(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a TotalFilledOrder at the ask price.")

      val filledOrder = TotalFilledOrder(bidOrder.issuer, askOrder.issuer, askPrice, quantity, 1,
        testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
      matchingEngine.bidOrderBook.isEmpty should be(true)

    }

    scenario("A limit bid order crosses an existing limit ask order with a greater quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val askQuantity = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLong(prng, lower = askPrice)
      val bidQuantity = randomLong(prng, upper = askQuantity)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder(bidOrder.issuer, askOrder.issuer, askPrice,
        bidQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.isEmpty should be(true)

      // also need to check that residual ask order landed in the book
      val residualAskQuantity = askQuantity - bidQuantity
      val residualAskOrder = askOrder.split(residualAskQuantity)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(residualAskOrder))

    }

    scenario("A limit ask order crosses an existing market bid order with a lesser quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing market and limit bid orders on its book...")
      val bidPrice = randomLong(prng)
      val limitBidQuantity = randomLong(prng)
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, limitBidQuantity, randomLong(prng),
        testTradable)
      matchingEngine.fill(limitBidOrder)

      val marketBidQuantity = randomLong(prng, upper = limitBidQuantity)
      val marketBidOrder = MarketBidOrder(bidOrderIssuer, marketBidQuantity, randomLong(prng),
        testTradable)
      matchingEngine.fill(marketBidOrder)

      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(marketBidOrder, limitBidOrder))

      When("an incoming LimitAskOrder crosses the existing market bid order...")
      val askPrice = randomLong(prng, upper = bidPrice)
      val askQuantity = randomLong(prng, marketBidQuantity, limitBidQuantity)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a PartialFilledOrder at the best limit price.")

      val partialFilledOrder = PartialFilledOrder(askOrder.issuer, marketBidOrder.issuer,
        bidPrice, marketBidQuantity, 1, testTradable)

      val residualAskQuantity = askQuantity - marketBidQuantity
      val residualAskOrder = askOrder.split(residualAskQuantity)
      val totalFilledOrder = TotalFilledOrder(askOrder.issuer, limitBidOrder.issuer, bidPrice,
        residualAskQuantity, 1, testTradable)
      val expectedFilledOrders = immutable.Queue(partialFilledOrder, totalFilledOrder)
      filledOrders should equal(Some(expectedFilledOrders))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = limitBidQuantity - residualAskQuantity
      val residualBidOrder = limitBidOrder.split(residualBidQuantity)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(residualBidOrder))

    }

    scenario("A market bid order crosses an existing limit ask order with a greater quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val askQuantity = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidQuantity = randomLong(prng, upper = askQuantity)
      val bidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a TotalFilledOrder")

      val filledOrder = TotalFilledOrder(bidOrder.issuer, askOrder.issuer, askPrice,
        bidQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue(filledOrder)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.isEmpty should be(true)

      // also need to check that residual ask order landed in the book
      val residualAskQuantity = askQuantity - bidQuantity
      val residualAskOrder = askOrder.split(residualAskQuantity)
      matchingEngine.askOrderBook.toSeq should equal(immutable.Seq(residualAskOrder))

    }

    scenario("A limit bid order crosses an existing limit ask order with a lesser quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val askQuantity = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLong(prng, lower = askPrice)
      val bidQuantity = randomLong(prng, lower = askQuantity)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a PartialFilledOrder")

      val filledOrder = PartialFilledOrder(bidOrder.issuer, askOrder.issuer, askPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      val residualBidOrder = bidOrder.split(residualBidQuantity)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(residualBidOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
    }

    scenario("A market bid order crosses an existing limit ask order with a lesser quantity.") {

      val matchingEngine = new CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLong(prng)
      val askQuantity = randomLong(prng)
      val askOrder = LimitAskOrder(bidOrderIssuer, askPrice, askQuantity, randomLong(prng), testTradable)
      matchingEngine.fill(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidQuantity = randomLong(prng, lower = askQuantity)
      val bidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, randomLong(prng), testTradable)
      val filledOrders = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a PartialFilledOrder")

      val filledOrder = PartialFilledOrder(bidOrder.issuer, askOrder.issuer, askPrice,
        askQuantity, 1, testTradable)
      filledOrders should equal(Some(immutable.Queue[FilledOrder](filledOrder)))

      // also need to check that residual bid order landed in the book
      val residualBidQuantity = bidQuantity - askQuantity
      val residualBidOrder = bidOrder.split(residualBidQuantity)
      matchingEngine.bidOrderBook.toSeq should equal(immutable.Seq(residualBidOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.isEmpty should be(true)
    }
  }
}
