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
package markets

import markets.engines.Matching
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.tradables.Tradable
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class FillSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng = new Random

  val tradable = Tradable("GOOG")

  feature("A Fill instance should be able to be created from a Matching instance.") {

    Given("some Matching instance,")
    val askPrice = randomLimitPrice(prng)
    val askQuantity = randomQuantity(prng)
    val ask = LimitAskOrder(uuid(), askPrice, askQuantity, timestamp(), tradable, uuid())
    val bidPrice = randomLimitPrice(prng, lower=askPrice)
    val bidQuantity = randomQuantity(prng)
    val bid = LimitBidOrder(uuid(), bidPrice, bidQuantity, timestamp(), tradable, uuid())

    val price = (askPrice / 2) + (bidPrice / 2)  // watch out for overflow!!
    val filledQuantity = Math.min(askQuantity, bidQuantity)
    val residualQuantity = Math.max(askQuantity, bidQuantity) - filledQuantity
    val residualAsk = if (ask.quantity > bid.quantity) Some(ask.split(residualQuantity)._2) else None
    val residualBid = if (bid.quantity > ask.quantity) Some(bid.split(residualQuantity)._2) else None

    val matching = Matching(ask, bid, price, filledQuantity, residualAsk, residualBid)

    Then("that Matching instance should be used to create a Fill instance.")

    val fill = Fill.fromMatching(matching, timestamp(), uuid())
    fill.askOrder should be(ask)
    fill.bidOrder should be(bid)
    fill.price should be(price)
    fill.quantity should be(filledQuantity)

  }

}
