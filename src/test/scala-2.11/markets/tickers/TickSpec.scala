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
package markets.tickers

import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.tradables.Tradable
import markets.{Fill, MarketsTestKit}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random


class TickSpec extends FeatureSpec
  with MarketsTestKit
  with GivenWhenThen
  with Matchers {

  val prng = new Random()

  feature("A Tick instance should be able to be created from a Fill instance.") {

    Given("a some Fill message,")
    val askPrice = randomLimitPrice()
    val askQuantity = randomQuantity()
    val ask = LimitAskOrder(uuid(), askPrice, askQuantity, timestamp(), validTradable, uuid())

    val bidPrice = randomLimitPrice(lower=askPrice)
    val bidQuantity = randomQuantity()
    val bid = LimitBidOrder(uuid(), bidPrice, bidQuantity, timestamp(), validTradable, uuid())

    val price = (askPrice + bidPrice) / 2
    val filledQuantity = Math.min(askQuantity, bidQuantity)
    val residualQuantity = Math.max(askQuantity, bidQuantity) - filledQuantity
    val residualAsk = if (ask.quantity > bid.quantity) Some(ask.split(residualQuantity)._2) else None
    val residualBid = if (bid.quantity > ask.quantity) Some(bid.split(residualQuantity)._2) else None

    val fill = Fill(ask, bid, price, filledQuantity, residualAsk, residualBid, timestamp(), uuid())

    Then("that Fill message can be used to create a Tick instance.")

    val tick = Tick.fromFill(fill)
    tick.askPrice should be(askPrice)
    tick.bidPrice should be(bidPrice)
    tick.price should be(price)
    tick.quantity should be(filledQuantity)

  }

}
