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
package markets.orders

import markets.MarketsTestKit
import markets.tradables.Security
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class PricedOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng = new Random()

  feature("An Tradable with Price object must have a non-negative price.") {

    scenario("Creating an Tradable with Price with negative price.") {

      val testTradable: Security = Security(uuid())

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLimitPrice()
      intercept[IllegalArgumentException](
        TestPricedOrder(uuid(), negativePrice, randomQuantity(), timestamp(), testTradable, uuid())
      )

    }

  }

  feature("Tradable with Price objects should be comparable based on price.") {

    scenario("Tradable with Price objects should be ordered according to price.") {
      val testTradable: Security = Security(uuid())
      val highPrice = randomLimitPrice()
      val highPriceOrder = TestPricedOrder(uuid(), highPrice, randomQuantity(), timestamp(), testTradable, uuid())
      val lowPrice = randomLimitPrice(upper = highPrice)
      val lowPriceOrder = TestPricedOrder(uuid(), lowPrice, randomQuantity(), timestamp(), testTradable, uuid())

      assert(Price.ordering.lt(lowPriceOrder, highPriceOrder))
    }

    scenario("Tradable with Price objects with same price but different uuids should be ordered according to uuid.") {
      val testTradable: Security = Security(uuid())
      val price = randomLimitPrice()
      val uuid1 = uuid()
      val order1 = TestPricedOrder(uuid(), price, randomQuantity(), timestamp(), testTradable, uuid1)
      val uuid2 = uuid()
      val order2 = TestPricedOrder(uuid(), price, randomQuantity(), timestamp(), testTradable, uuid2)

      assert(if (uuid1.compareTo(uuid2) <= 0) Price.ordering.lteq(order1, order2) else Price.ordering.gt(order1, order2))
    }

  }

}
