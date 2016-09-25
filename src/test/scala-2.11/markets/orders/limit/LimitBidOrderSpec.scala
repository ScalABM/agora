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
package markets.orders.limit

import markets.MarketsTestKit
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class LimitBidOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng: Random = new Random()

  feature("A LimitBidOrder object must have a strictly positive price.") {

    scenario("Creating a LimitBidOrder with negative price.") {

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = -randomLimitPrice()
      intercept[IllegalArgumentException](
        LimitBidOrder(uuid(), negativePrice, randomQuantity(), timestamp(), validTradable, uuid())
      )

    }

    scenario("Creating a LimitBidOrder with zero price.") {

      When("a LimitBidOrder with a zero price is constructed an exception is thrown.")

      intercept[IllegalArgumentException](
        LimitBidOrder(uuid(), 0, randomQuantity(), timestamp(), validTradable, uuid())
      )

    }

  }

  feature("A LimitBidOrder should be able to cross with other orders.") {

    val bidOrder = randomBidOrder(marketOrderProbability = 0.0, tradable = validTradable)

    scenario("A LimitBidOrder should cross with any MarketAskOrder.") {
      val askOrder = randomAskOrder(marketOrderProbability = 1.0, tradable = validTradable)
      assert(bidOrder.isAcceptable(askOrder))
    }

    scenario("A LimitBidOrder should cross with any LimitAskOrder with a higher price.") {
      val askOrder = randomAskOrder(marketOrderProbability = 0.0, maximumPrice = bidOrder.price, tradable = validTradable)
      assert(bidOrder.isAcceptable(askOrder))
    }

    scenario("A LimitBidOrder should not cross with any LimitAskOrder with a lower price.") {
      val askOrder = randomAskOrder(marketOrderProbability = 0.0, minimumPrice = bidOrder.price, tradable = validTradable)
      assert(!bidOrder.isAcceptable(askOrder))
    }

    scenario("A LimitBidOrder should not cross with any LimitAskOrder for another tradable.") {
      val askOrder = randomAskOrder(marketOrderProbability = 0.0, minimumPrice = bidOrder.price, tradable = invalidTradable)
      assert(!bidOrder.isAcceptable(askOrder))

    }
  }

}
