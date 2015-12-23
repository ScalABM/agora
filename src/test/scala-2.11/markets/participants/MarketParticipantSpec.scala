/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestKit, TestProbe, TestActorRef}

import java.util.UUID
import markets.engines.BrokenMatchingEngine
import markets.tickers.Tick
import markets.{Filled, Remove, Add, MarketActor, Canceled, Accepted}
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketBidOrder
import markets.tradables.{TestTradable, Security}
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global


/** Test specification for a `MarketParticiantActor` actor. */
class MarketParticipantSpec extends TestKit(ActorSystem("MarketParticipantSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  def timestamp(): Long = {
    System.currentTimeMillis()
  }

  def uuid(): UUID = {
    UUID.randomUUID()
  }

  feature("A MarketParticipant actor should be able to add and remove outstanding orders.") {

    val tradable = Security("GOOG")

    scenario("A MarketParticipant actor receives an Accepted message...") {

      val marketParticipant = TestActorRef(new TestMarketParticipant)
      val order = LimitAskOrder(marketParticipant, 10, 100, timestamp(), tradable, uuid())

      When("A MarketParticipant actor receives an Accepted message...")
      val accepted = Accepted(order, timestamp(), uuid())
      marketParticipant ! accepted

      Then("...it should add the accepted orders UUID to its outstanding orders.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.outstandingOrders.headOption should be(Some(order))

    }

    scenario("A MarketParticipant actor receives a Canceled message...") {

      val marketParticipant = TestActorRef(new TestMarketParticipant)

      Given("A MarketParticipant actor with outstanding orders...")
      val order = LimitAskOrder(marketParticipant, 10, 100, 1, tradable, uuid())
      val accepted = Accepted(order, 2, uuid())
      marketParticipant ! accepted

      When("A MarketParticipant actor receives a Canceled message...")
      val canceled = Canceled(order, 3, uuid())
      marketParticipant ! canceled

      Then("...it should remove the canceled order from its outstanding orders.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.outstandingOrders.headOption should be(None)

    }

    scenario("A MarketParticipant actor receives a Filled message...") {

      val marketParticipant = TestActorRef(new TestMarketParticipant)

      Given("A MarketParticipant actor with outstanding orders...")
      val order1 = LimitAskOrder(marketParticipant, 10, 100, timestamp(), tradable, uuid())
      val order2 = MarketBidOrder(marketParticipant, 1000, timestamp(), tradable, uuid())
      val accepteds = immutable.Seq(Accepted(order1, timestamp(), uuid()),
        Accepted(order2, timestamp(), uuid()))
      accepteds.foreach(accepted => marketParticipant ! accepted)

      When("A MarketParticipant actor receives a Filled message with no residual order...")
      val filled = Filled(order1, None, timestamp(), uuid())
      marketParticipant ! filled

      Then("...it should remove the filled order from its outstanding orders.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.outstandingOrders.headOption should be(Some(order2))

      When("A MarketParticipant actor receives a Filled message with some residual order...")
      val(_, residualOrder) = order2.split(500)
      val partialFilled = Filled(order2, Some(residualOrder), timestamp(), uuid())
      marketParticipant ! partialFilled

      Then("...it should remove the original filled order from its outstanding orders and replace" +
        " it with the residual order.")
      marketParticipantActor.outstandingOrders.headOption should be(Some(residualOrder))

    }

  }

  feature("A MarketParticipant actor should be able to add and remove markets.") {

    val marketParticipant = TestActorRef(new TestMarketParticipant)

    val matchingEngine = new BrokenMatchingEngine()
    val settlementMechanism = TestProbe()
    val ticker = Agent(Tick(1, 1, Some(1), 1, 1))
    val tradable = TestTradable("GOOG")
    val marketProps = MarketActor.props(matchingEngine, settlementMechanism.ref, ticker, tradable)
    val market = TestActorRef(marketProps)

    scenario("A MarketParticipant actor receives an Add message...") {

      When("A MarketParticipant actor receives an Add message...")
      val add = Add(market, ticker, timestamp(), tradable, uuid())
      marketParticipant ! add

      Then("...it should add the market to its collection of markets.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.markets(tradable) should be(market)
      marketParticipantActor.tickers(tradable) should be(ticker)

    }

    scenario("A MarketParticipant actor receives a Remove message...") {

      val add = Add(market, ticker, timestamp(), tradable, uuid())
      marketParticipant ! add

      When("A MarketParticipant actor receives a Remove message...")
      val remove = Remove(timestamp(), tradable, uuid())
      marketParticipant ! remove

      Then("...it should remove the market from its collection of markets.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.markets.isEmpty should be(true)
      marketParticipantActor.tickers.isEmpty should be(true)

    }

  }

}
