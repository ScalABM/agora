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
package markets.actors.settlement

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}

import markets.actors.Filled
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.tradables.Tradable
import markets.{Fill, MarketsTestKit}
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.util.Random


class SettlementMechanismActorSpec extends TestKit(ActorSystem("SettlementMechanismActorSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  val askIssuer = TestProbe()

  val bidIssuer = TestProbe()

  val prng = new Random

  val tradable = Tradable("GOOG")

  feature("A SettlementMechanismActor should be able to process Fill messages.") {

    val settlementMechanism = system.actorOf(Props[TestSettlementMechanismActor])

    When("a SettlementMechanismActor receives a Fill message,")
    val askPrice = randomLimitPrice(prng)
    val askQuantity = randomQuantity(prng)
    val ask = LimitAskOrder(askIssuer.ref, askPrice, askQuantity, timestamp(), tradable, uuid())

    val bidPrice = randomLimitPrice(prng, lower=askPrice)
    val bidQuantity = randomQuantity(prng)
    val bid = LimitBidOrder(bidIssuer.ref, bidPrice, bidQuantity, timestamp(), tradable, uuid())

    val price = (askPrice + bidPrice) / 2
    val filledQuantity = Math.min(askQuantity, bidQuantity)
    val residualQuantity = Math.max(askQuantity, bidQuantity) - filledQuantity
    val residualAsk = if (ask.quantity > bid.quantity) Some(ask.split(residualQuantity)._2) else None
    val residualBid = if (bid.quantity > ask.quantity) Some(bid.split(residualQuantity)._2) else None

    val fill = Fill(ask, bid, price, filledQuantity, residualAsk, residualBid, timestamp(), uuid())

    settlementMechanism ! fill

    Then("that SettlementMechanismActor should notify the issuers of the ask and bid orders.")

    askIssuer.expectMsgType[Filled]
    bidIssuer.expectMsgType[Filled]

  }

}
