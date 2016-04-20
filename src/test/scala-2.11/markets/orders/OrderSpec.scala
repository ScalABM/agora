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
package markets.orders

import akka.actor.ActorSystem
import akka.testkit.TestKit

import markets.MarketsTestKit
import markets.tradables.Security
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.util.Random


class OrderSpec extends TestKit(ActorSystem("OrderLikeSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  feature("An Order object must have a non-negative price and strictly positive quantity.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("Creating an order with negative price or non-positive quantity.") {

      val testTradable: Security = Security("AAPL")

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLimitPrice(prng, lower, upper)
      intercept[IllegalArgumentException](
        TestOrder(testActor, negativePrice, randomQuantity(prng, lower, upper), timestamp(),
          testTradable, uuid())
      )

      When("an order with a non-positive quantity is constructed an exception is thrown.")

      val negativeQuantity = -randomQuantity(prng, lower, upper)
      intercept[IllegalArgumentException](
        TestOrder(testActor, randomLimitPrice(prng, lower, upper), negativeQuantity, timestamp(),
          testTradable, uuid())
      )

      val zeroQuantity = 0
      intercept[IllegalArgumentException](
        TestOrder(testActor, randomLimitPrice(prng, lower, upper), zeroQuantity, timestamp(),
          testTradable, uuid())
      )

    }

    scenario("Creating an order whose price is not a multiple of the tick.") {

      val tick = 10
      val testTradable: Security = Security("AAPL", tick)

      When("an order whose price is not a multiple of the tick an exception is thrown.")

      val invalidPrice = tick + 1  // make sure that price is not divisible by tick!
      intercept[IllegalArgumentException](
        TestOrder(testActor, invalidPrice, randomQuantity(prng, lower, upper), timestamp(),
          testTradable, uuid())
      )
    }
  }
}
