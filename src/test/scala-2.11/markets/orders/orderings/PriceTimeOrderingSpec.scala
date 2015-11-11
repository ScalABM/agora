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

import akka.actor.ActorSystem
import akka.testkit.TestKit
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}


class PriceTimeOrderingSpec extends TestKit(ActorSystem("PriceTimeOrderingSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers with
  BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  /** Maximum share price for testing. */
  val maxPrice = 1000.0

  /** Maximum number of share for testing. */
  val maxQuantity = 1000000

  val testTradable = TestTradable("AAPL")

  feature("A SortedAskOrderBook with PriceTimeAskOrdering should maintain price priority") {

/*    scenario("Multiple limit ask orders are added to an empty ask order book.") {

      Given("An empty ask order book,")

      val askOrderBook = SortedAskOrderBook(testTradable)(PriceTimeAskOrdering)

      When("two limit ask orders are added to the book with the lower priced order first,")

      val lowPrice = Random.nextDouble() * maxPrice
      val quantity1 = Random.nextInt(maxQuantity)
      val ask1 = LimitAskOrder(testActor, testTradable, lowPrice, quantity1)

      val highPrice = (1 + Random.nextDouble()) * lowPrice
      val quantity2 = Random.nextInt(maxQuantity)
      val ask2 = LimitAskOrder(testActor, testTradable, highPrice, quantity2)

      askOrderBook += (ask1, ask2)

      Then("the lower priced order should be at the top of the ask order book queue.")

      askOrderBook.dequeue() should be(ask1)
      askOrderBook.dequeue() should be(ask2)
      askOrderBook.headOption should be(None)

      Given("An empty ask order book,")

      assert(askOrderBook.isEmpty)

      When("that two limit orders asks are added to the book with the higher priced order first,")

      askOrderBook += (ask2, ask1)

      Then("the lower priced order should be at the top of the ask order book queue.")

      askOrderBook.dequeue() should be(ask1)
      askOrderBook.dequeue() should be(ask2)
      askOrderBook.headOption should be(None)

    }

    scenario("An aggressive limit ask order lands in an ask order book with existing orders.") {

      val askOrderBook = SortedAskOrderBook(testTradable)

      Given("An ask order book that contains existing orders")

      val lowPrice = Random.nextDouble() * maxPrice
      val quantity1 = Random.nextInt(maxQuantity)
      val existingAsk1 = LimitAskOrder(testActor, testTradable, lowPrice, quantity1)

      val highPrice = (1 + Random.nextDouble()) * lowPrice
      val quantity2 = Random.nextInt(maxQuantity)
      val existingAsk2 = LimitAskOrder(testActor, testTradable, highPrice, quantity2)

      askOrderBook += (existingAsk1, existingAsk2)

      When("an aggressive limit ask order lands in the book,")

      val aggressivePrice = Random.nextDouble() * lowPrice
      val quantity3 = Random.nextInt(maxQuantity)
      val aggressiveAsk = LimitAskOrder(testActor, testTradable, aggressivePrice, quantity3)

      askOrderBook += aggressiveAsk

      Then("the aggressive limit ask order should be at the top of the ask order book queue.")

      askOrderBook.head should be(aggressiveAsk)
      askOrderBook.clear()

    }

    scenario("A passive limit ask order lands in an ask order book with existing orders.") {

      val askOrderBook = SortedAskOrderBook(testTradable)

      Given("An ask order book that contains existing orders")

      val lowPrice = Random.nextDouble() * maxPrice
      val quantity1 = Random.nextInt(maxQuantity)
      val existingAsk1 = LimitAskOrder(testActor, testTradable, lowPrice, quantity1)

      val highPrice = (1 + Random.nextDouble()) * lowPrice
      val quantity2 = Random.nextInt(maxQuantity)
      val existingAsk2 = LimitAskOrder(testActor, testTradable, highPrice, quantity2)

      askOrderBook += (existingAsk1, existingAsk2)

      When("a passive limit ask order lands in the book,")

      val passivePrice = 0.5 * (lowPrice + highPrice)
      val quantity3 = Random.nextInt(maxQuantity)
      val passiveAsk = LimitAskOrder(testActor, testTradable, passivePrice, quantity3)

      askOrderBook += passiveAsk

      Then("the ask order book should maintain price priority.")

      askOrderBook.dequeue() should be(existingAsk1)
      askOrderBook.dequeue() should be(passiveAsk)
      askOrderBook.dequeue() should be(existingAsk2)

    }

  }

  feature("An SortedAskOrderBook should maintaining time priority.") {

    scenario("A limit ask order lands in an ask order book with existing orders.") {

      val askOrderBook = SortedAskOrderBook(testTradable)

      Given("An ask order book that contains existing orders")

      val lowPrice = Random.nextDouble() * maxPrice
      val quantity1 = Random.nextInt(maxQuantity)
      val existingAsk1 = LimitAskOrder(testActor, testTradable, lowPrice, quantity1)

      val highPrice = (1 + Random.nextDouble()) * lowPrice
      val quantity2 = Random.nextInt(maxQuantity)
      val existingAsk2 = LimitAskOrder(testActor, testTradable, highPrice, quantity2)

      askOrderBook +=(existingAsk1, existingAsk2)

      When("a limit ask order at the same price as the best existing limit ask order,")

      val quantity3 = Random.nextInt(maxQuantity)
      val incomingAsk = LimitAskOrder(testActor, testTradable, lowPrice, quantity3)

      askOrderBook += incomingAsk

      Then("the best existing limit ask order should be at the top of the ask order book queue.")

      askOrderBook.dequeue() should be(existingAsk1)
      askOrderBook.dequeue() should be(incomingAsk)
      askOrderBook.dequeue() should be(existingAsk2)

    }

  }

*/
  }

}
