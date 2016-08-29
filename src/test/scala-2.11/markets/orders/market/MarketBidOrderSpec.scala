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
package markets.orders.market

import markets.MarketsTestKit
import markets.tradables.Tradable
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class MarketBidOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng: Random = new Random()

  feature("A MarketBidOrder should be able to cross with other orders.") {

    val bidOrder = randomBidOrder(marketOrderProbability = 1.0, tradable = validTradable)

    scenario("A MarketBidOrder should cross with any MarketAskOrder.") {
      val askOrder = randomAskOrder(marketOrderProbability = 1.0, tradable = validTradable)
      assert(bidOrder.crosses(askOrder))
    }

    scenario("A MarketBidOrder should cross with any LimitAskOrder.") {
      val askOrder = randomAskOrder(marketOrderProbability = 0.0, tradable = validTradable)
      assert(bidOrder.crosses(askOrder))
    }

    scenario("A MarketBidOrder should not cross with any AskOrder for another tradable.") {

      val askOrder = randomAskOrder(tradable = invalidTradable)
      intercept[MatchError](
        bidOrder.crosses(askOrder)
      )

    }
  }

  feature("A MarketBidOrder should be able to split itself into two separate orders.") {

    val bidOrder = randomBidOrder(marketOrderProbability = 1.0, tradable = validTradable)

    val filledQuantity = randomQuantity(upper=bidOrder.quantity)
    val residualQuantity = bidOrder.quantity - filledQuantity
    val (filledOrder, residualOrder) = bidOrder.split(residualQuantity)

    filledOrder.quantity should be(filledQuantity)
    residualOrder.quantity should be(residualQuantity)

  }

}
