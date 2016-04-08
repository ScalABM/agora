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
package markets.participants

import akka.actor.{ActorRef, ActorSystem}
import akka.agent.Agent
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import java.util.UUID

import markets.tickers.Tick
import markets.{Add, Filled, MarketsTestKit, Remove}
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketBidOrder
import markets.tradables.{TestTradable, Tradable}
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global


/** Test specification for a `MarketParticipantActor` actor. */
class MarketParticipantSpec extends TestKit(ActorSystem("MarketParticipantSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }


  feature("A MarketParticipant actor should be able to add and remove outstanding orders.") {

    val tradable = TestTradable("GOOG")
    val market = TestProbe()
    val markets = mutable.Map[Tradable, ActorRef](tradable -> market.ref)

    val initialTick = Tick(1, 1, 1, 1, timestamp())
    val tickers = mutable.Map[Tradable, Agent[Tick]](tradable -> Agent(initialTick))

    val props = TestMarketParticipant.props(markets, tickers)
    val marketParticipantRef = TestActorRef[MarketParticipant](props)
    val marketParticipantActor = marketParticipantRef.underlyingActor

    scenario("A MarketParticipant actor receives a Filled message...") {

      Given("A MarketParticipant actor with outstanding orders...")
      val order1 = LimitAskOrder(marketParticipantRef, 10, 100, timestamp(), tradable, uuid())
      val order2 = MarketBidOrder(marketParticipantRef, 1000, timestamp(), tradable, uuid())
      marketParticipantActor.outstandingOrders += (order1, order2)

      When("A MarketParticipant actor receives a Filled message with no residual order...")
      val filled = Filled(order1, None, timestamp(), uuid())
      marketParticipantRef ! filled

      Then("...it should remove the filled order from its outstanding orders.")
      marketParticipantActor.outstandingOrders.headOption should be(Some(order2))

      When("A MarketParticipant actor receives a Filled message with some residual order...")
      val(_, residualOrder) = order2.split(500)
      val partialFilled = Filled(order2, Some(residualOrder), timestamp(), uuid())
      marketParticipantRef ! partialFilled

      Then("...it should remove the original filled order from its outstanding orders and replace" +
        " it with the residual order.")
      marketParticipantActor.outstandingOrders.headOption should be(Some(residualOrder))

    }

  }

  feature("A MarketParticipant actor should be able to add and remove markets.") {

    val markets = mutable.Map.empty[Tradable, ActorRef]
    val tickers = mutable.Map.empty[Tradable, Agent[Tick]]
    val props = TestMarketParticipant.props(markets, tickers)
    val marketParticipantRef = TestActorRef[MarketParticipant](props)

    val market = testActor
    val ticker = Agent(Tick(1, 1, 1, 1, timestamp()))
    val tradable = TestTradable("GOOG")

    scenario("A MarketParticipant actor receives an Add message...") {

      val add = Add(market, ticker, timestamp(), tradable, uuid())

      When("A MarketParticipant actor receives an Add message...")
      marketParticipantRef ! add

      Then("...it should add the market to its collection of markets.")
      val marketParticipantActor = marketParticipantRef.underlyingActor
      marketParticipantActor.markets(tradable) should be(market)
      marketParticipantActor.tickers(tradable) should be(ticker)

    }

    scenario("A MarketParticipant actor receives a Remove message...") {

      val add = Add(market, ticker, timestamp(), tradable, uuid())
      marketParticipantRef ! add

      When("A MarketParticipant actor receives a Remove message...")
      val remove = Remove(timestamp(), tradable, uuid())
      marketParticipantRef ! remove

      Then("...it should remove the market from its collection of markets.")
      val marketParticipantActor = marketParticipantRef.underlyingActor
      assert(marketParticipantActor.markets.isEmpty)
      assert(marketParticipantActor.tickers.isEmpty)

    }

  }

}
