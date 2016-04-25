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

import markets.{Add, MarketsTestKit, Remove}
import markets.tickers.Tick
import markets.tradables.{TestTradable, Tradable}
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}


class MarketParticipantSpec extends TestKit(ActorSystem("MarketParticipantSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  val tradable = TestTradable("GOOG")

  val market = TestProbe()
  val markets = Map[Tradable, ActorRef](tradable -> market.ref)

  val initialTick = Tick(1, 1, 1, 1, timestamp())
  val tickers = Map[Tradable, Agent[Tick]](tradable -> Agent(initialTick)(system.dispatcher))

  feature("A MarketParticipant should be able to add and remove markets.") {

    val markets = Map.empty[Tradable, ActorRef]
    val tickers = Map.empty[Tradable, Agent[Tick]]
    val props = TestMarketParticipant.props(markets, tickers)
    val marketParticipantRef = TestActorRef[TestMarketParticipant](props)
    val marketParticipantActor = marketParticipantRef.underlyingActor

    scenario("A MarketParticipant receives an Add message...") {

      val market = testActor
      val ticker = Agent(initialTick)(system.dispatcher)
      val add = Add(market, ticker, timestamp(), tradable, uuid())

      When("A MarketParticipant receives an Add message...")
      marketParticipantRef ! add

      Then("...it should add the market to its collection of markets.")
      marketParticipantActor.markets(tradable) should be(market)
      marketParticipantActor.tickers(tradable) should be(ticker)

    }

    scenario("A MarketParticipant receives a Remove message...") {

      When("A MarketParticipant receives a Remove message...")
      val remove = Remove(timestamp(), tradable, uuid())
      marketParticipantRef ! remove

      Then("...it should remove the market from its collection of markets.")
      marketParticipantActor.markets.isEmpty should be(true)
      marketParticipantActor.tickers.isEmpty should be(true)

    }

  }
}
