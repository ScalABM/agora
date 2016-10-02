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
package markets.tradables.orders

import markets.MarketsTestKit
import markets.tradables.{Security, Tradable}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


/** Class used to test the basic functionality of a `BidOrder`. */
class AskOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng = new Random()

  val tradable: Tradable = Security(uuid())

  feature("An AskOrder must have a non-negative price.") {

    scenario("Creating an AskOrder with negative price.") {

      When("an AskOrder with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLimitPrice()
      intercept[IllegalArgumentException](
        TestAskOrder(uuid(), negativePrice, randomQuantity(), timestamp(), tradable, uuid())
      )

    }

  }

  feature("An AskOrder object must have strictly positive quantity.") {

    scenario("Creating an AskOrder with non-positive quantity.") {

      val negativeQuantity = -randomQuantity()
      intercept[IllegalArgumentException](
        TestAskOrder(uuid(), randomLimitPrice(), negativeQuantity, timestamp(), tradable, uuid())
      )

      val zeroQuantity = 0
      intercept[IllegalArgumentException](
        TestAskOrder(uuid(), randomLimitPrice(), zeroQuantity, timestamp(), tradable, uuid())
      )

    }

  }

  feature("AskOrder with lower price should be less than an AskOrder with higher price.") {

    scenario("Comparing two AskOrder objects with different prices.") {
      val highPrice = randomLimitPrice()
      val highPriceOrder = TestAskOrder(uuid(), highPrice, randomQuantity(), timestamp(), tradable, uuid())
      val lowPrice = randomLimitPrice(upper = highPrice)
      val lowPriceOrder = TestAskOrder(uuid(), lowPrice, randomQuantity(), timestamp(), tradable, uuid())

      assert(AskOrder.ordering.lt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("AskOrder objects with same price should be ordered by uuid.") {

    scenario("Comparing two AskOrder objects with the same price.") {
      val price = randomLimitPrice()
      val uuid1 = uuid()
      val order1 = TestAskOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid1)
      val uuid2 = uuid()
      val order2 = TestAskOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid2)

      assert(if (uuid1.compareTo(uuid2) <= 0) AskOrder.ordering.lteq(order1, order2) else AskOrder.ordering.gt(order1, order2))
    }

  }

  feature("AskOrder with lower price should have priority over AskOrder with higher price.") {

    scenario("Comparing two AskOrder objects with different prices.") {
      val highPrice = randomLimitPrice()
      val highPriceOrder = TestAskOrder(uuid(), highPrice, randomQuantity(), timestamp(), tradable, uuid())
      val lowPrice = randomLimitPrice(upper = highPrice)
      val lowPriceOrder = TestAskOrder(uuid(), lowPrice, randomQuantity(), timestamp(), tradable, uuid())
      assert(AskOrder.priority.gt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("AskOrder objects with same price should have priority determined by uuid.") {

    scenario("Comparing two AskOrder objects with the same price.") {
      val price = randomLimitPrice()
      val uuid1 = uuid()
      val order1 = TestAskOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid1)
      val uuid2 = uuid()
      val order2 = TestAskOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid2)
      assert(if (uuid1.compareTo(uuid2) <= 0) AskOrder.priority.gteq(order1, order2) else AskOrder.priority.lt(order1, order2))
    }

  }

}
