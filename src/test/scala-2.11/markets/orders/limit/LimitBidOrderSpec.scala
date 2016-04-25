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
package markets.orders.limit

import akka.actor.ActorSystem
import akka.testkit.TestKit

import markets.MarketsTestKit
import markets.orders.market.MarketAskOrder
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.util.Random


class LimitBidOrderSpec extends TestKit(ActorSystem("MarketBidOrderSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  val prng: Random = new Random()

  val tradable: TestTradable = TestTradable("AAPL")

  feature("A LimitBidOrder should be able to cross with other orders.") {

    val price = randomLimitPrice(prng)
    val quantity = randomQuantity(prng)
    val bidOrder = LimitBidOrder(testActor, price, quantity, timestamp(), tradable, uuid())

    scenario("A LimitBidOrder should cross with any MarketAskOrder.") {
      val askQuantity = randomQuantity(prng)
      val askOrder = MarketAskOrder(testActor, askQuantity, timestamp(), tradable, uuid())
      bidOrder.crosses(askOrder) should be(true)
    }

    scenario("A LimitBidOrder should cross with any LimitAskOrder with a lower price.") {
      val askPrice = randomLimitPrice(prng, upper=price)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, timestamp(), tradable, uuid())
      bidOrder.crosses(askOrder) should be(true)
    }

    scenario("A LimitBidOrder should not cross with any LimitAskOrder with a higher price.") {
      val askPrice = randomLimitPrice(prng, lower=price)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, timestamp(), tradable, uuid())
      bidOrder.crosses(askOrder) should be(false)
    }

    scenario("A LimitBidOrder should not cross with any LimitAskOrder for another tradable.") {

      val otherTradable = TestTradable("GOOG")
      val askPrice = randomLimitPrice(prng, lower=price)
      val askQuantity = randomQuantity(prng)
      val askOrder = LimitAskOrder(testActor, askPrice, askQuantity, timestamp(), otherTradable, uuid())

      intercept[MatchError](
        bidOrder.crosses(askOrder)
      )

    }
  }

  feature("A LimitBidOrder should be able to split itself into two separate orders.") {

    scenario("Splitting a LimitBidOrder into two orders.") {

      val price = randomLimitPrice(prng)
      val quantity = randomLimitPrice(prng)
      val limitOrder = LimitBidOrder(testActor, price, quantity, timestamp(), tradable, uuid())

      val filledQuantity = randomQuantity(prng, upper=quantity)
      val residualQuantity = quantity - filledQuantity
      val (filledOrder, residualOrder) = limitOrder.split(residualQuantity)

      filledOrder.quantity should be(filledQuantity)
      residualOrder.quantity should be(residualQuantity)

    }

  }

}
