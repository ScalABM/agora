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
package org.economicsl.agora.tradables.orders.bid

import org.economicsl.agora.OrderGenerator
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}


class MarketBidOrderSpec extends FeatureSpec with GivenWhenThen with Matchers with OrderGenerator {

  feature("A MarketBidOrder should be able to cross with other orders.") {

    val bidOrder = orderGenerator.nextMarketBidOrder(None, validTradable)

    scenario("A MarketBidOrder should cross with any MarketAskOrder.") {
      val askOrder = orderGenerator.nextMarketAskOrder(None, validTradable)
      assert(bidOrder.isAcceptable(askOrder))
    }

    scenario("A MarketBidOrder should cross with any LimitAskOrder.") {
      val askOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      assert(bidOrder.isAcceptable(askOrder))
    }

    scenario("A MarketBidOrder should not cross with any AskOrder for another tradable.") {

      val askOrder = orderGenerator.nextMarketAskOrder(None, invalidTradable)
      assert(!bidOrder.isAcceptable(askOrder))

    }
  }

}
