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
import markets.orders.{MarketBidOrder, LimitBidOrder}
import markets.orders.orderings.BidTimeOrdering
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}


class SortedBidOrderBookSpec extends TestKit(ActorSystem("SortedBidOrderBookSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers with
  BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  val testTradable = TestTradable("AAPL")

  feature("A SortedBidOrderBook should be able to find a best limit bid order.") {

    scenario("A SortedBidOrderBook with BidTimeOrdering containing at least one limit order.") {

      Given("A SortedBidOrderBook that contains at least one limit order")

      val existingBid1 = MarketBidOrder(testActor, 100, 2, testTradable)
      val existingBid2 = LimitBidOrder(testActor, 10, 1, 1, testTradable)

      val bidOrderBook = SortedBidOrderBook(testTradable)(BidTimeOrdering())
      bidOrderBook += (existingBid1, existingBid2)

      Then("the SortedBidOrderBook should have a best limit order.")

      bidOrderBook.bestLimitOrder should be(Some(existingBid2))

      Given("A SortedBidOrderBook that contains multiple limit order")

      val existingBid3 = LimitBidOrder(testActor, 100, 1, 3, testTradable)
      bidOrderBook += existingBid3

      Then("the best limit order should be the one with the lowest timestamp.")

      bidOrderBook.bestLimitOrder should be(Some(existingBid2))
      bidOrderBook.clear()

    }

    scenario("An empty SortedBidOrderBook should not have a best limit order") {

      Given("An empty SortedBidOrderBook")

      val bidOrderBook = SortedBidOrderBook(testTradable)(BidTimeOrdering())

      Then("the best existing limit bid order should be None.")

      bidOrderBook.bestLimitOrder should be(None)

    }

    scenario("A SortedBidOrderBook containing only market orders should not have a best limit order") {

      Given("A SortedBidOrderBook containing only market orders")

      val bidOrderBook = SortedBidOrderBook(testTradable)(BidTimeOrdering())
      bidOrderBook += (MarketBidOrder(testActor, 1, 1, testTradable), MarketBidOrder(testActor, 100, 1, testTradable))

      Then("the best existing limit bid order should be None.")

      bidOrderBook.bestLimitOrder should be(None)

    }
  }
}
