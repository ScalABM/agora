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


class MarketAskOrderSpec extends FeatureSpec with GivenWhenThen with Matchers with OrderGenerator {

  feature("A MarketAskOrder should be able to cross with other orders.") {

    val askOrder = orderGenerator.randomMarketAskOrder(validTradable)

    scenario("A MarketAskOrder should cross with any MarketBidOrder.") {
      val bidOrder = orderGenerator.randomMarketBidOrder(validTradable)
      assert(askOrder.isAcceptable(bidOrder))
    }

    scenario("A MarketAskOrder should cross with any LimitBidOrder.") {
      val bidOrder = orderGenerator.randomLimitBidOrder(validTradable)
      assert(askOrder.isAcceptable(bidOrder))
    }

    scenario("A MarketAskOrder should not cross with any BidOrder for another tradable.") {

      val bidOrder = orderGenerator.randomMarketBidOrder(invalidTradable)
      assert(!askOrder.isAcceptable(bidOrder))

    }
  }

}
