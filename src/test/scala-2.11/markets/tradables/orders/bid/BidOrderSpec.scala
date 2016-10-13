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
package markets.tradables.orders.bid

import markets.tradables.TestTradable
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}


/** Class used to test the basic functionality of a `BidOrder`. */
class BidOrderSpec extends FeatureSpec with GivenWhenThen with Matchers {

  val tradable = TestTradable()

  feature("An BidOrder must have a non-negative price.") {

    scenario("Creating an BidOrder with negative price.") {

      When("an BidOrder with a negative price is constructed an exception is thrown.")

      val (negativePrice, quantity) = (-1, 1)
      intercept[IllegalArgumentException](
        TestBidOrder(negativePrice, quantity, tradable)
      )

    }

  }

  feature("A BidOrder object must have strictly positive quantity.") {

    scenario("Creating an BidOrder with non-positive quantity.") {

      val (price, negativeQuantity) = (1, -1)
      intercept[IllegalArgumentException](
        TestBidOrder(price, negativeQuantity, tradable)
      )

      val zeroQuantity = 0
      intercept[IllegalArgumentException](
        TestBidOrder(price, zeroQuantity, tradable)
      )

    }

  }

  feature("BidOrder with higher price should be less than a BidOrder with lower price.") {

    scenario("Comparing two BidOrder objects with different prices.") {
      val highPrice = 1000
      val highPriceOrder = TestBidOrder(highPrice, tradable=tradable)
      val lowPrice = 500
      val lowPriceOrder = TestBidOrder(lowPrice, tradable=tradable)

      assert(BidOrder.ordering.gt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("BidOrder objects with same price should be ordered by uuid.") {

    scenario("Comparing two BidOrder objects with the same price.") {
      val price = 1000
      val order1 = TestBidOrder(price, tradable=tradable)
      val order2 = TestBidOrder(price, tradable=tradable)

      assert(if (order1.uuid.compareTo(order2.uuid) <= 0) BidOrder.ordering.gteq(order1, order2) else BidOrder.ordering.lt(order1, order2))
    }

  }

  feature("BidOrder with higher price should have priority over BidOrder with lower price.") {

    scenario("Comparing two BidOrder objects with different prices.") {
      val highPrice = 2000
      val highPriceOrder = TestBidOrder(highPrice, tradable=tradable)
      val lowPrice = 1000
      val lowPriceOrder = TestBidOrder(lowPrice, tradable=tradable)
      assert(BidOrder.priority.lt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("BidOrder objects with same price should have priority determined by uuid.") {

    scenario("Comparing two BidOrder objects with the same price.") {
      val price = 3546
      val order1 = TestBidOrder(price, tradable=tradable)
      val order2 = TestBidOrder(price, tradable=tradable)

      assert(if (order1.uuid.compareTo(order2.uuid) <= 0) BidOrder.priority.lteq(order1, order2) else BidOrder.priority.gt(order1, order2))
    }

  }

}
