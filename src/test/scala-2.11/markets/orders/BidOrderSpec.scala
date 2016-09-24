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


/** Class used to test the basic functionality of a `BidOrder`. */
class BidOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng = new Random()

  val tradable: Security = Security(uuid())

  feature("A BidOrder object must have strictly positive quantity.") {

    scenario("Creating an BidOrder with non-positive quantity.") {

      val negativeQuantity = -randomQuantity()
      intercept[IllegalArgumentException](
        TestBidOrder(uuid(), randomLimitPrice(), negativeQuantity, timestamp(), tradable, uuid())
      )

      val zeroQuantity = 0
      intercept[IllegalArgumentException](
        TestBidOrder(uuid(), randomLimitPrice(), zeroQuantity, timestamp(), tradable, uuid())
      )

    }

  }

  feature("BidOrder with higher price should be less than a BidOrder with lower price.") {

    scenario("Comparing two BidOrder objects with different prices.") {
      val highPrice = randomLimitPrice()
      val highPriceOrder = TestBidOrder(uuid(), highPrice, randomQuantity(), timestamp(), tradable, uuid())
      val lowPrice = randomLimitPrice(upper = highPrice)
      val lowPriceOrder = TestBidOrder(uuid(), lowPrice, randomQuantity(), timestamp(), tradable, uuid())

      assert(BidOrder.ordering.gt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("BidOrder objects with same price should be ordered by uuid.") {

    scenario("Comparing two BidOrder objects with the same price.") {
      val price = randomLimitPrice()
      val uuid1 = uuid()
      val order1 = TestBidOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid1)
      val uuid2 = uuid()
      val order2 = TestBidOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid2)

      assert(if (uuid1.compareTo(uuid2) <= 0) BidOrder.ordering.gteq(order1, order2) else BidOrder.ordering.lt(order1, order2))
    }

  }

  feature("BidOrder with higher price should have priority over BidOrder with lower price.") {

    scenario("Comparing two BidOrder objects with different prices.") {
      val highPrice = randomLimitPrice()
      val highPriceOrder = TestBidOrder(uuid(), highPrice, randomQuantity(), timestamp(), tradable, uuid())
      val lowPrice = randomLimitPrice(upper = highPrice)
      val lowPriceOrder = TestBidOrder(uuid(), lowPrice, randomQuantity(), timestamp(), tradable, uuid())
      assert(BidOrder.priority.lt(lowPriceOrder, highPriceOrder))
    }

  }

  feature("BidOrder objects with same price should have priority determined by uuid.") {

    scenario("Comparing two BidOrder objects with the same price.") {
      val price = randomLimitPrice()
      val uuid1 = uuid()
      val order1 = TestBidOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid1)
      val uuid2 = uuid()
      val order2 = TestBidOrder(uuid(), price, randomQuantity(), timestamp(), tradable, uuid2)
      assert(if (uuid1.compareTo(uuid2) <= 0) BidOrder.priority.lteq(order1, order2) else BidOrder.priority.gt(order1, order2))
    }

  }

}
