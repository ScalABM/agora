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
package markets.orders

import akka.actor.ActorSystem
import akka.testkit.TestKit

import java.util.UUID

import markets.tradables.Security
import org.scalatest.{FeatureSpecLike, Matchers, BeforeAndAfterAll, GivenWhenThen}

import scala.util.Random


class OrderSpec extends TestKit(ActorSystem("OrderLikeSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers with
  BeforeAndAfterAll {

  /** Shutdown actor system when finished. */
  override def afterAll(): Unit = {
    system.terminate()
  }

  def randomLong(prng: Random, lower: Long, upper: Long): Long = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }

  def uuid: UUID = {
    UUID.randomUUID()
  }

  feature("An Order object must have a non-negative price and strictly positive quantity.") {

    val lower: Long = 1
    val upper: Long = Long.MaxValue
    val prng: Random = new Random()

    scenario("Creating an order with negative price or non-positive quantity.") {

      val testTradable: Security = Security("AAPL")

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLong(prng, lower, upper)
      intercept[IllegalArgumentException](
        TestOrder(testActor, negativePrice, randomLong(prng, lower, upper),
          randomLong(prng, lower, upper), testTradable, uuid)
      )

      When("an order with a non-positive quantity is constructed an exception is thrown.")

      val negativeQuantity = -randomLong(prng, lower, upper)
      intercept[IllegalArgumentException](
        TestOrder(testActor, randomLong(prng, lower, upper), negativeQuantity,
          randomLong(prng, lower, upper), testTradable, uuid)
      )

      val zeroQuantity = 0
      intercept[IllegalArgumentException](
        TestOrder(testActor, randomLong(prng, lower, upper), zeroQuantity,
          randomLong(prng, lower, upper), testTradable, uuid)
      )

    }

    scenario("Creating an order whose price is not a multiple of the tick.") {

      val tick = 10
      val testTradable: Security = Security("AAPL", tick)

      When("an order whose price is not a multiple of the tick an exception is thrown.")

      val invalidPrice = tick + prng.nextInt(tick - 1)
      intercept[IllegalArgumentException](
        TestOrder(testActor, invalidPrice, randomLong(prng, lower, upper),
          randomLong(prng, lower, upper), testTradable, uuid)
      )
    }
  }
}
