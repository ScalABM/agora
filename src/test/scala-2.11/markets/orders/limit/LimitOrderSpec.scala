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
import markets.orders.market.MarketBidOrder
import markets.tradables.TestTradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.util.Random


class LimitOrderSpec extends TestKit(ActorSystem("MarketOrderSpec"))
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

  feature("A LimitOrder object must have a strictly positive price.") {

    scenario("Creating an order with negative price.") {

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLimitPrice(prng)
      intercept[IllegalArgumentException](
        TestLimitOrder(testActor, negativePrice, randomQuantity(prng), timestamp(), tradable, uuid())
      )

    }

  }

}
