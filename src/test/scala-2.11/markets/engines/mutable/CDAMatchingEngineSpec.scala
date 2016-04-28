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
package markets.engines.mutable

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestKit

import markets.MarketsTestKit
import markets.engines.Matching
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.tradables.Security
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random


class CDAMatchingEngineSpec extends TestKit(ActorSystem("CDAMatchingEngineSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  val testTradable: Security = Security("AAPL")

  val askOrderIssuer: ActorRef = testActor

  val bidOrderIssuer: ActorRef = testActor

  feature("A MutableTreeSetCDAMatchingEngine matching engine should be able to generate matches orders") {

    val prng: Random = new Random()

    scenario("A new limit ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a LimitAskOrder arrives...")
      val price = randomLimitPrice(prng)
      val quantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, price, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("it should land in the ask order book")
      matchings should be(None)
      matchingEngine.askOrderBook.headOption should be(Some(askOrder))

    }

    scenario("A new market ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a MarketAskOrder arrives...")
      val quantity = randomQuantity(prng)
      val askOrder = MarketAskOrder(askOrderIssuer, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("it should land in the ask order book.")
      matchings should be(None)
      matchingEngine.askOrderBook.headOption should be(Some(askOrder))

    }

    scenario("A new limit bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a LimitBidOrder arrives...")
      val price = randomLimitPrice(prng)
      val quantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, price, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("it should land in the bid order book.")

      matchings should be(None)
      matchingEngine.bidOrderBook.headOption should be(Some(bidOrder))

    }

    scenario("A new market bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      When("a MarketBidOrder arrives...")
      val quantity = randomQuantity(prng)
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("it should land in the bid order book.")

      matchings should be(None)
      matchingEngine.bidOrderBook.headOption should be(Some(bidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with the same quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val quantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(prng, upper = bidPrice)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching at the bid price.")

      val matching = Matching(askOrder, bidOrder, bidPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.headOption should be(None)
      matchingEngine.bidOrderBook.headOption should be(None)

    }

    scenario("A limit ask order crosses an existing market bid order with the same quantity.") {

      val initialPrice = 1
      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, initialPrice)

      Given("a matching engine with only existing market bid order on its book...")
      val quantity = randomQuantity(prng)
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching at the reference price.")

      val price = math.max(initialPrice, askPrice)
      val matching = Matching(askOrder, bidOrder, price, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.headOption should be(None)
      matchingEngine.bidOrderBook.headOption should be(None)

    }

    scenario("A market ask order crosses an existing limit bid order with the same quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val quantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askOrder = MarketAskOrder(askOrderIssuer, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching at the bid price.")

      val matching = Matching(askOrder, bidOrder, bidPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.headOption should be(None)
      matchingEngine.bidOrderBook.headOption should be(None)

    }

    scenario("A limit ask order crosses an existing limit bid order with a greater quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val bidQuantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(prng, upper = bidPrice)
      val askQuantity = randomQuantity(prng, upper = bidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.headOption should be(Some(residualBidOrder))

    }

    scenario("A limit ask order crosses an existing market bid order with a greater quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing market and limit bid orders on its book...")
      val bidPrice = randomLimitPrice(prng)
      val bidQuantity = randomQuantity(prng)
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(),
        testTradable, uuid())
      matchingEngine.findMatch(limitBidOrder)

      val marketBidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(marketBidOrder)

      When("an incoming LimitAskOrder crosses the existing market bid order...")
      val askPrice = randomLimitPrice(prng, upper = bidPrice)
      val askQuantity = randomQuantity(prng, upper = bidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching at the best limit price.")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = marketBidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, marketBidOrder, bidPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.headOption should be(Some(residualBidOrder))

    }

    scenario("A market ask order crosses an existing limit bid order with a greater quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val bidQuantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askQuantity = randomQuantity(prng, upper = bidQuantity)
      val askOrder = MarketAskOrder(askOrderIssuer, askQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching")
      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.headOption should be(Some(residualBidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with a lesser quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val bidQuantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(prng, upper = bidPrice)
      val askQuantity = randomQuantity(prng, lower = bidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.headOption should be(Some(residualAskOrder))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.headOption should be(None)
    }

    scenario("A market ask order crosses an existing limit bid order with a lesser quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val bidQuantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askQuantity = randomQuantity(prng, lower = bidQuantity)
      val askOrder = MarketAskOrder(askOrderIssuer, askQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.headOption should be(Some(residualAskOrder))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.headOption should be(None)
    }

    scenario("A market ask order crosses an existing market bid order with the same quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice(prng)
      val limitBidQuantity = randomQuantity(prng)
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, limitBidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(limitBidOrder)

      val marketBidQuantity = randomQuantity(prng)
      val marketBidOrder = MarketBidOrder(bidOrderIssuer, marketBidQuantity, timestamp(),
        testTradable, uuid())
      matchingEngine.findMatch(marketBidOrder)

      When("an incoming MarketAskOrder crosses the existing market bid order...")
      val askOrder = MarketAskOrder(askOrderIssuer, marketBidQuantity, timestamp(),
        testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching")

      val matching = Matching(askOrder, marketBidOrder, bidPrice, marketBidQuantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      matchingEngine.bidOrderBook.headOption should be(Some(limitBidOrder))
      matchingEngine.askOrderBook.headOption should be(None)

    }

    scenario("A limit bid order crosses an existing limit ask order with the same quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice(prng)
      val quantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLimitPrice(prng, lower = askPrice)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching")

      val matching = Matching(askOrder, bidOrder, askPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.headOption should be(None)
      matchingEngine.bidOrderBook.headOption should be(None)

    }

    scenario("A market bid order crosses an existing limit ask order with the same quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice(prng)
      val quantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching at the ask price.")

      val matching = Matching(askOrder, bidOrder, askPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.headOption should be(None)
      matchingEngine.bidOrderBook.headOption should be(None)

    }

    scenario("A market bid order crosses an existing market ask order with the same quantity.") {
      val referencePrice = 1
      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, referencePrice)

      Given("a matching engine with existing limit and market ask orders on its book...")
      val askPrice = randomLimitPrice(prng)
      val limitQuantity = randomQuantity(prng)
      val limitAskOrder = LimitAskOrder(askOrderIssuer, askPrice, limitQuantity, timestamp(),
        testTradable, uuid())
      matchingEngine.findMatch(limitAskOrder)

      val marketQuantity = randomQuantity(prng)
      val marketAskOrder = MarketAskOrder(askOrderIssuer, marketQuantity, timestamp(),
        testTradable, uuid())
      matchingEngine.findMatch(marketAskOrder)

      When("an incoming MarketBidOrder crosses an existing market ask order...")
      val bidOrder = MarketBidOrder(bidOrderIssuer, marketQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching at the lesser of the best limit ask " +
        "price and the reference price")

      val price = math.min(limitAskOrder.price, referencePrice)
      val matching = Matching(marketAskOrder, bidOrder, price, marketQuantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      matchingEngine.askOrderBook.headOption should be(Some(limitAskOrder))
      matchingEngine.bidOrderBook.headOption should be(None)

    }

    scenario("A limit bid order crosses an existing limit ask order with a greater quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice(prng)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLimitPrice(prng, lower = askPrice)
      val bidQuantity = randomQuantity(prng, upper = askQuantity)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.headOption should be(None)

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.headOption should be(Some(residualAskOrder))

    }

    scenario("A limit ask order crosses an existing market bid order with a lesser quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing market and limit bid orders on its book...")
      val bidPrice = randomLimitPrice(prng)
      val limitBidQuantity = randomQuantity(prng)
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, limitBidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(limitBidOrder)

      val marketBidQuantity = randomQuantity(prng, upper = limitBidQuantity)
      val marketBidOrder = MarketBidOrder(bidOrderIssuer, marketBidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(marketBidOrder)

      When("an incoming LimitAskOrder crosses the existing market bid order...")
      val askPrice = randomLimitPrice(prng, upper = bidPrice)
      val askQuantity = randomQuantity(prng, marketBidQuantity, limitBidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(askOrder)

      Then("the matching engine should generate a Matching at the best limit price.")

      val residualAskQuantity = askQuantity - marketBidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val partialFill = Matching(askOrder, marketBidOrder, bidPrice, marketBidQuantity, Some(residualAskOrder), None)

      val residualBidQuantity = limitBidQuantity - residualAskQuantity
      val (_, residualBidOrder) = limitBidOrder.split(residualBidQuantity)
      val totalFill = Matching(residualAskOrder, limitBidOrder, bidPrice, residualAskQuantity,
        None, Some(residualBidOrder))
      val expectedFilledOrders = immutable.Queue(partialFill, totalFill)
      matchings should equal(Some(expectedFilledOrders))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.headOption should be(Some(residualBidOrder))

    }

    scenario("A market bid order crosses an existing limit ask order with a greater quantity.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice(prng)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidQuantity = randomQuantity(prng, upper = askQuantity)
      val bidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue(matching)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.headOption should be(None)

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.headOption should be(Some(residualAskOrder))

    }

    scenario("A limit bid order crosses an existing limit ask order with a lesser quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice(prng)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLimitPrice(prng, lower = askPrice)
      val bidQuantity = randomQuantity(prng, lower = askQuantity)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.headOption should be(Some(residualBidOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)
    }

    scenario("A market bid order crosses an existing limit ask order with a lesser quantity.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice(prng)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidQuantity = randomQuantity(prng, lower = askQuantity)
      val bidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, timestamp(), testTradable, uuid())
      val matchings = matchingEngine.findMatch(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.headOption should be(Some(residualBidOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)
    }
  }

  feature("A MutableTreeSetCDAMatchingEngine should be able to remove and order from the order book.") {

    val prng: Random = new Random()

    scenario("A MutableTreeSetCDAMatchingEngine attempts to remove an existing order from its order book.") {

      val matchingEngine =  MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a MutableTreeSetCDAMatchingEngine with an existing limit order on its book...")
      val askPrice = randomLimitPrice(prng)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      Then("...the MutableTreeSetCDAMatchingEngine should be able to remove that order.")
      val result = matchingEngine.pop(askOrder)
      result should be(Some(askOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)

    }

    scenario("A MutableTreeSetCDAMatchingEngine attempts to remove an order from its order book.") {

      val matchingEngine = MutableTreeSetCDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

      Given("a MutableTreeSetCDAMatchingEngine with an existing orders on its book...")
      val askPrice = randomLimitPrice(prng)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(askOrder)

      val bidPrice = randomLimitPrice(prng, lower = askPrice)
      val bidQuantity = askQuantity
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), testTradable, uuid())
      matchingEngine.findMatch(bidOrder)

      When("...the MutableTreeSetCDAMatchingEngine to remove and order that has already been filled...")
      val result = matchingEngine.pop(askOrder)
      result should be(None)

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.headOption should be(None)

    }
  }
}
