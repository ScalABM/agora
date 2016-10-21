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
package markets.tradables.orders.ask

import markets.OrderGenerator
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}


/** Class used to test the basic functionality of an `AskOrder`. */
class AskOrderSpec extends FeatureSpec with GivenWhenThen with Matchers with OrderGenerator {

  feature("An AskOrder must have a non-negative price.") {

    scenario("Creating an AskOrder with negative price.") {

      When("an AskOrder with a negative price is constructed an exception is thrown.")

      val negativePrice = -1
      intercept[IllegalArgumentException](
        orderGenerator.randomLimitAskOrder(negativePrice, None, validTradable)
      )

    }

  }

  feature("AskOrder with lower price should be less than an AskOrder with higher price.") {

    scenario("Comparing two AskOrder objects with different prices.") {
      val highPrice = 500
      val highPriceOrder = orderGenerator.randomLimitAskOrder(highPrice, None, validTradable)
      val lowPrice = 250
      val lowPriceOrder = orderGenerator.randomLimitAskOrder(lowPrice, None, validTradable)

      assert(AskOrder.ordering.lt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("AskOrder objects with same price should be ordered by uuid.") {

    scenario("Comparing two AskOrder objects with the same price.") {
      val price = 100
      val order1 = orderGenerator.randomLimitAskOrder(price, None, validTradable)
      val order2 = orderGenerator.randomLimitAskOrder(price, None, validTradable)

      assert(if (order1.uuid.compareTo(order2.uuid) <= 0) AskOrder.ordering.lteq(order1, order2) else AskOrder.ordering.gt(order1, order2))
    }

  }

  feature("AskOrder with lower price should have priority over AskOrder with higher price.") {

    scenario("Comparing two AskOrder objects with different prices.") {
      val highPrice = 600
      val highPriceOrder = orderGenerator.randomLimitAskOrder(highPrice, None, validTradable)
      val lowPrice = 300
      val lowPriceOrder = orderGenerator.randomLimitAskOrder(lowPrice, None, validTradable)
      assert(AskOrder.priority.gt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("AskOrder objects with same price should have priority determined by uuid.") {

    scenario("Comparing two AskOrder objects with the same price.") {
      val price = 5000
      val order1 = orderGenerator.randomLimitAskOrder(price, None, validTradable)
      val order2 = orderGenerator.randomLimitAskOrder(price, None, validTradable)
      assert(if (order1.uuid.compareTo(order2.uuid) <= 0) AskOrder.priority.gteq(order1, order2) else AskOrder.priority.lt(order1, order2))
    }

  }

}
