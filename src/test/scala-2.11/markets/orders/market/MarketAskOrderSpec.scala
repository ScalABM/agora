/*
Copyright 2016 David R. Pugh

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
import markets.orders.limit.LimitBidOrder
import markets.tradables.Tradable
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class MarketAskOrderSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterAll {

  val prng: Random = new Random()

  val tradable = Tradable("AAPL")

  feature("A MarketAskOrder should be able to cross with other orders.") {

    val price = randomLimitPrice(prng)
    val quantity = randomQuantity(prng)
    val askOrder = MarketAskOrder(uuid(), quantity, timestamp(), tradable, uuid())

    scenario("A MarketAskOrder should cross with any MarketBidOrder.") {
      val bidQuantity = randomQuantity(prng)
      val bidOrder = MarketBidOrder(uuid(), bidQuantity, timestamp(), tradable, uuid())
      askOrder.crosses(bidOrder) should be(true)
    }

    scenario("A MarketAskOrder should cross with any LimitBidOrder.") {
      val bidPrice = randomLimitPrice(prng)
      val bidQuantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(uuid(), bidPrice, bidQuantity, timestamp(), tradable, uuid())
      askOrder.crosses(bidOrder) should be(true)
    }

    scenario("A MarketAskOrder should not cross with any BidOrder for another tradable.") {

      val otherTradable = Tradable("GOOG")
      val bidPrice = randomLimitPrice(prng, lower=price)
      val bidQuantity = randomQuantity(prng)
      val bidOrder = LimitBidOrder(uuid(), bidPrice, bidQuantity, timestamp(), otherTradable, uuid())

      intercept[MatchError](
        askOrder.crosses(bidOrder)
      )

    }
  }

  feature("A MarketAskOrder should be able to split itself into two separate orders.") {

    val quantity = randomLimitPrice(prng)
    val askOrder = MarketAskOrder(uuid(), quantity, timestamp(), tradable, uuid())

    val filledQuantity = randomQuantity(prng, upper=quantity)
    val residualQuantity = quantity - filledQuantity
    val (filledOrder, residualOrder) = askOrder.split(residualQuantity)

    filledOrder.quantity should be(filledQuantity)
    residualOrder.quantity should be(residualQuantity)

  }

}
