/*
Copyright 2016 ScalABM

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
package markets.engines

import markets.MarketsTestKit
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.util.Random


class CDAMatchingEngineSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {


  def prng = new Random()

  val askOrderIssuer = uuid()

  val bidOrderIssuer = uuid()

  val initialPrice = 1

  feature("A CDAMatchingEngine matching engine should be able to generate matches.") {

    val prng: Random = new Random()

    scenario("A new limit ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      When("a LimitAskOrder arrives...")
      val price = randomLimitPrice()
      val quantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, price, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("it should land in the ask order book")
      matchings should be(None)
      matchingEngine.askOrderBook.peek should be(Some(askOrder))

    }

    scenario("A new market ask order lands in an empty order book.") {

      Given("a matching engine with an empty ask order book...")

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      When("a MarketAskOrder arrives...")
      val quantity = randomQuantity()
      val askOrder = MarketAskOrder(askOrderIssuer, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("it should land in the ask order book.")
      matchings should be(None)
      matchingEngine.askOrderBook.peek should be(Some(askOrder))

    }

    scenario("A new limit bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      When("a LimitBidOrder arrives...")
      val price = randomLimitPrice()
      val quantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, price, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("it should land in the bid order book.")

      matchings should be(None)
      matchingEngine.bidOrderBook.peek should be(Some(bidOrder))

    }

    scenario("A new market bid order lands in an empty order book.") {

      Given("a matching engine with an empty bid order book...")

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      When("a MarketBidOrder arrives...")
      val quantity = randomQuantity()
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("it should land in the bid order book.")

      matchings should be(None)
      matchingEngine.bidOrderBook.peek should be(Some(bidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with the same quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val quantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(upper = bidPrice)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching at the bid price.")

      val matching = Matching(askOrder, bidOrder, bidPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.peek should be(None)
      matchingEngine.bidOrderBook.peek should be(None)

    }

    scenario("A limit ask order crosses an existing market bid order with the same quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with only existing market bid order on its book...")
      val quantity = randomQuantity()
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching at the reference price.")

      val price = math.max(initialPrice, askPrice)
      val matching = Matching(askOrder, bidOrder, price, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.peek should be(None)
      matchingEngine.bidOrderBook.peek should be(None)

    }

    scenario("A market ask order crosses an existing limit bid order with the same quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val quantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askOrder = MarketAskOrder(askOrderIssuer, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching at the bid price.")

      val matching = Matching(askOrder, bidOrder, bidPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.peek should be(None)
      matchingEngine.bidOrderBook.peek should be(None)

    }

    scenario("A limit ask order crosses an existing limit bid order with a greater quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val bidQuantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(upper = bidPrice)
      val askQuantity = randomQuantity(upper = bidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.peek should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.peek should be(Some(residualBidOrder))

    }

    scenario("A limit ask order crosses an existing market bid order with a greater quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing market and limit bid orders on its book...")
      val bidPrice = randomLimitPrice()
      val bidQuantity = randomQuantity()
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(),
        validTradable, uuid())
      matchingEngine.fill(limitBidOrder)

      val marketBidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(marketBidOrder)

      When("an incoming LimitAskOrder crosses the existing market bid order...")
      val askPrice = randomLimitPrice(upper = bidPrice)
      val askQuantity = randomQuantity(upper = bidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching at the best limit price.")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = marketBidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, marketBidOrder, bidPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.peek should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.peek should be(Some(residualBidOrder))

    }

    scenario("A market ask order crosses an existing limit bid order with a greater quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val bidQuantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askQuantity = randomQuantity(upper = bidQuantity)
      val askOrder = MarketAskOrder(askOrderIssuer, askQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching")
      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.peek should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.peek should be(Some(residualBidOrder))

    }

    scenario("A limit ask order crosses an existing limit bid order with a lesser quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val bidQuantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming LimitAskOrder crosses the existing limit bid order...")
      val askPrice = randomLimitPrice(upper = bidPrice)
      val askQuantity = randomQuantity(lower = bidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.peek should be(Some(residualAskOrder))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.peek should be(None)
    }

    scenario("A market ask order crosses an existing limit bid order with a lesser quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val bidQuantity = randomQuantity()
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(bidOrder)

      When("an incoming MarketAskOrder crosses the existing limit bid order...")
      val askQuantity = randomQuantity(lower = bidQuantity)
      val askOrder = MarketAskOrder(askOrderIssuer, askQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, bidPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.peek should be(Some(residualAskOrder))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.peek should be(None)
    }

    scenario("A market ask order crosses an existing market bid order with the same quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit bid order on its book...")
      val bidPrice = randomLimitPrice()
      val limitBidQuantity = randomQuantity()
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, limitBidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(limitBidOrder)

      val marketBidQuantity = randomQuantity()
      val marketBidOrder = MarketBidOrder(bidOrderIssuer, marketBidQuantity, timestamp(),
        validTradable, uuid())
      matchingEngine.fill(marketBidOrder)

      When("an incoming MarketAskOrder crosses the existing market bid order...")
      val askOrder = MarketAskOrder(askOrderIssuer, marketBidQuantity, timestamp(),
        validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

      Then("the matching engine should generate a Matching")

      val matching = Matching(askOrder, marketBidOrder, bidPrice, marketBidQuantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      matchingEngine.bidOrderBook.peek should be(Some(limitBidOrder))
      matchingEngine.askOrderBook.peek should be(None)

    }

    scenario("A limit bid order crosses an existing limit ask order with the same quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice()
      val quantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLimitPrice(lower = askPrice)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching")

      val matching = Matching(askOrder, bidOrder, askPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.peek should be(None)
      matchingEngine.bidOrderBook.peek should be(None)

    }

    scenario("A market bid order crosses an existing limit ask order with the same quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice()
      val quantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, quantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidOrder = MarketBidOrder(bidOrderIssuer, quantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching at the ask price.")

      val matching = Matching(askOrder, bidOrder, askPrice, quantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that order books are now empty
      matchingEngine.askOrderBook.peek should be(None)
      matchingEngine.bidOrderBook.peek should be(None)

    }

    scenario("A market bid order crosses an existing market ask order with the same quantity.") {
      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with existing limit and market ask orders on its book...")
      val askPrice = randomLimitPrice()
      val limitQuantity = randomQuantity()
      val limitAskOrder = LimitAskOrder(askOrderIssuer, askPrice, limitQuantity, timestamp(),
        validTradable, uuid())
      matchingEngine.fill(limitAskOrder)

      val marketQuantity = randomQuantity()
      val marketAskOrder = MarketAskOrder(askOrderIssuer, marketQuantity, timestamp(),
        validTradable, uuid())
      matchingEngine.fill(marketAskOrder)

      When("an incoming MarketBidOrder crosses an existing market ask order...")
      val bidOrder = MarketBidOrder(bidOrderIssuer, marketQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching at the lesser of the best limit ask " +
        "price and the reference price")

      val price = math.min(limitAskOrder.price, initialPrice)
      val matching = Matching(marketAskOrder, bidOrder, price, marketQuantity, None, None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      matchingEngine.askOrderBook.peek should be(Some(limitAskOrder))
      matchingEngine.bidOrderBook.peek should be(None)

    }

    scenario("A limit bid order crosses an existing limit ask order with a greater quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice()
      val askQuantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLimitPrice(lower = askPrice)
      val bidQuantity = randomQuantity(upper = askQuantity)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.peek should be(None)

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.peek should be(Some(residualAskOrder))

    }

    scenario("A limit ask order crosses an existing market bid order with a lesser quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing market and limit bid orders on its book...")
      val bidPrice = randomLimitPrice()
      val limitBidQuantity = randomQuantity()
      val limitBidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, limitBidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(limitBidOrder)

      val marketBidQuantity = randomQuantity(upper = limitBidQuantity)
      val marketBidOrder = MarketBidOrder(bidOrderIssuer, marketBidQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(marketBidOrder)

      When("an incoming LimitAskOrder crosses the existing market bid order...")
      val askPrice = randomLimitPrice(upper = bidPrice)
      val askQuantity = randomQuantity(marketBidQuantity, limitBidQuantity)
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(askOrder)

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
      matchingEngine.askOrderBook.peek should be(None)

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.peek should be(Some(residualBidOrder))

    }

    scenario("A market bid order crosses an existing limit ask order with a greater quantity.") {

      val matchingEngine = CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice()
      val askQuantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidQuantity = randomQuantity(upper = askQuantity)
      val bidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualAskQuantity = askQuantity - bidQuantity
      val (_, residualAskOrder) = askOrder.split(residualAskQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, bidQuantity, Some(residualAskOrder), None)
      matchings should equal(Some(immutable.Queue(matching)))

      // also should check that bid order book is now empty
      matchingEngine.bidOrderBook.peek should be(None)

      // also need to check that residual ask order landed in the book
      matchingEngine.askOrderBook.peek should be(Some(residualAskOrder))

    }

    scenario("A limit bid order crosses an existing limit ask order with a lesser quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice()
      val askQuantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(askOrder)

      When("an incoming LimitBidOrder crosses the existing limit ask order...")
      val bidPrice = randomLimitPrice(lower = askPrice)
      val bidQuantity = randomQuantity(lower = askQuantity)
      val bidOrder = LimitBidOrder(bidOrderIssuer, bidPrice, bidQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.peek should be(Some(residualBidOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.peek should be(None)
    }

    scenario("A market bid order crosses an existing limit ask order with a lesser quantity.") {

      val matchingEngine =  CDAMatchingEngine(initialPrice, validTradable)

      Given("a matching engine with an existing limit ask order on its book...")
      val askPrice = randomLimitPrice()
      val askQuantity = randomQuantity()
      val askOrder = LimitAskOrder(askOrderIssuer, askPrice, askQuantity, timestamp(), validTradable, uuid())
      matchingEngine.fill(askOrder)

      When("an incoming MarketBidOrder crosses the existing limit ask order...")
      val bidQuantity = randomQuantity(lower = askQuantity)
      val bidOrder = MarketBidOrder(bidOrderIssuer, bidQuantity, timestamp(), validTradable, uuid())
      val matchings = matchingEngine.fill(bidOrder)

      Then("the matching engine should generate a Matching")

      val residualBidQuantity = bidQuantity - askQuantity
      val (_, residualBidOrder) = bidOrder.split(residualBidQuantity)
      val matching = Matching(askOrder, bidOrder, askPrice, askQuantity, None, Some(residualBidOrder))
      matchings should equal(Some(immutable.Queue[Matching](matching)))

      // also need to check that residual bid order landed in the book
      matchingEngine.bidOrderBook.peek should be(Some(residualBidOrder))

      // also should check that ask order book is now empty
      matchingEngine.askOrderBook.peek should be(None)
    }
  }

}
