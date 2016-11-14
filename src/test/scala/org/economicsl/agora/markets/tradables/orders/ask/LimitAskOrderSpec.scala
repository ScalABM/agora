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
package org.economicsl.agora.markets.tradables.orders.ask

import org.economicsl.agora.OrderGenerator
import org.economicsl.agora.markets.tradables.Price
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}


class LimitAskOrderSpec extends FeatureSpec with GivenWhenThen with Matchers with OrderGenerator {

  feature("A LimitAskOrder object must have a strictly positive price.") {

    scenario("Creating a LimitAskOrder with negative price.") {

      When("an order with a negative price is constructed an exception is thrown.")

      val negativePrice = Price(-1)
      intercept[IllegalArgumentException](
        orderGenerator.nextLimitAskOrder(negativePrice, validTradable)
      )

    }

    scenario("Creating a LimitAskOrder with zero price.") {

      When("a LimitAskOrder with a zero price is constructed an exception is thrown.")

      val zeroPrice = Price(0)
      intercept[IllegalArgumentException](
        orderGenerator.nextLimitAskOrder(zeroPrice, validTradable)
      )

    }

  }

  feature("A LimitAskOrder should be able to cross with other orders.") {

    val askPrice = Price(100)
    val askOrder = orderGenerator.nextLimitAskOrder(askPrice, validTradable)

    scenario("A LimitAskOrder should cross with any MarketBidOrder.") {
      val bidOrder = orderGenerator.nextMarketBidOrder(validTradable)
      assert(askOrder.isAcceptable(bidOrder))
    }

    scenario("A LimitAskOrder should cross with any LimitBidOrder with a higher price.") {
      val bidPrice = Price(105)
      val bidOrder = orderGenerator.nextLimitBidOrder(bidPrice, validTradable)
      assert(askOrder.isAcceptable(bidOrder))
    }

    scenario("A LimitAskOrder should not cross with any LimitBidOrder with a lower price.") {
      val bidPrice = Price(95)
      val bidOrder = orderGenerator.nextLimitBidOrder(bidPrice, validTradable)
      assert(!askOrder.isAcceptable(bidOrder))
    }

    scenario("A LimitAskOrder should not cross with any LimitBidOrder for another tradable.") {
      val bidOrder = orderGenerator.nextLimitBidOrder(invalidTradable)
      assert(!askOrder.isAcceptable(bidOrder))
    }
  }

}
