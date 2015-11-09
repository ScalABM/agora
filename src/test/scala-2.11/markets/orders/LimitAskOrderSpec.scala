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
import markets.tradables.Tradable
import org.scalatest.{GivenWhenThen, FeatureSpecLike, Matchers}

import scala.util.Random


class LimitAskOrderSpec extends TestKit(ActorSystem("LimitAskOrderSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  /** Stub Tradable object for testing purposes. */
  case class TestTradable(ticker: String) extends Tradable

  feature("Crossing logic for a LimitAskOrder.") {

    scenario("Crossing a LimitAskOrder with a LimitBidOrder") {

      Given("some limit ask order")

      val askPrice = 10
      val askQuantity = 1
      val tradable = TestTradable("GOOG")
      val limitAskOrder = LimitAskOrder(testActor, askPrice, askQuantity, tradable)
      val anotherLimitAskOrder = LimitAskOrder(testActor, 5, askQuantity, tradable)

      Given("some limit bid order whose price exceeds that of the limit ask order")

      val bidPrice = (1 + Random.nextDouble()) * askPrice
      val bidQuantity = generateRandomQuantity(maxQuantity)
      val crossingLimitBidOrder = LimitBidOrder(testActor, testInstrument, bidPrice, bidQuantity)

      Then("that limit bid order should cross with the limit ask order.")
      limitAskOrder < anotherLimitAskOrder

      assert(limitAskOrder.crosses(crossingLimitBidOrder))

    }

  }

}