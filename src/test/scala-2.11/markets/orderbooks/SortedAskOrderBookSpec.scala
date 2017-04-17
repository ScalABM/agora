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
package markets.orderbooks

import akka.actor.ActorSystem
import akka.testkit.TestKit
import markets.orders.{MarketAskOrder, LimitAskOrder}
import markets.orders.orderings.AskTimeOrdering
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}


class SortedAskOrderBookSpec extends TestKit(ActorSystem("SortedAskOrderBookSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers with
  BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  val testTradable = TestTradable("AAPL")

  feature("A SortedAskOrderBook should be able to find a best limit ask order.") {

    scenario("A SortedAskOrderBook containing at least one limit order.") {

      Given("A SortedAskOrderBook that contains at least one limit order")

      val existingAsk1 = MarketAskOrder(testActor, 100, 1, testTradable)
      val existingAsk2 = LimitAskOrder(testActor, 10, 1, 1, testTradable)

      val askOrderBook = SortedAskOrderBook(testTradable)(AskTimeOrdering())
      askOrderBook += (existingAsk1, existingAsk2)

      Then("the SortedAskOrderBook should have a best limit order.")

      askOrderBook.bestLimitOrder should be(Some(existingAsk2))

      Given("A SortedAskOrderBook that contains multiple limit order")

      val existingAsk3 = LimitAskOrder(testActor, 100, 1, 1, testTradable)
      askOrderBook += existingAsk3

      Then("the best limit order should be the one with the lowest price.")

      askOrderBook.bestLimitOrder should be(Some(existingAsk2))
      askOrderBook.clear()

    }

    scenario("An empty SortedAskOrderBook should not have a best limit order") {

      Given("An empty SortedAskOrderBook")

      val askOrderBook = SortedAskOrderBook(testTradable)(AskTimeOrdering())

      Then("the best existing limit ask order should be None.")

      askOrderBook.bestLimitOrder should be(None)

    }

    scenario("A SortedAskOrderBook containing only market orders should not have a best limit order") {

      Given("A SortedAskOrderBook containing only market orders")

      val askOrderBook = SortedAskOrderBook(testTradable)(AskTimeOrdering())
      askOrderBook += (MarketAskOrder(testActor, 1, 1, testTradable), MarketAskOrder(testActor, 100, 1, testTradable))

      Then("the best existing limit ask order should be None.")

      askOrderBook.bestLimitOrder should be(None)

    }
  }
}
