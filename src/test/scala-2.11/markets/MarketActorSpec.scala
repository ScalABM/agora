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

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import java.util.UUID
import markets.engines.BrokenMatchingEngine
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tickers.Tick
import markets.tradables.TestTradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable

/** Test specification for a `MarketLike` actor.
  *
  * @note A `MarketLike` actor should directly receive `AskOrder` and `BidOrder` orders
  *       for a particular `Tradable` (filtering out any invalid orders) and then forward along
  *       all valid orders to a `ClearingMechanismLike` actor for further processing.
  */
class MarketActorSpec extends TestKit(ActorSystem("MarketActorSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  def uuid(): UUID = {
    UUID.randomUUID()
  }

  feature("A MarketActor should receive and process Order messages.") {

    val marketParticipant = TestProbe()

    // create a matching engine
    val matchingEngine = new BrokenMatchingEngine()
    val settlementMechanism = TestProbe()

    // create an initial tick
    val initialTick = Tick(1, 1, 1, 1, System.currentTimeMillis())
    val ticker = Agent(initialTick)(system.dispatcher)

    val tradable = TestTradable("GOOG")
    val marketProps = MarketActor.props(matchingEngine, settlementMechanism.ref, ticker, tradable)
    val testMarket = TestActorRef(marketProps)

    scenario("A MarketActor receives valid Order messages.") {

      When("A MarketActor receives a valid Order message...")
      val validOrders = List(LimitAskOrder(marketParticipant.ref, 1, 1, 1, tradable, uuid()),
                             MarketBidOrder(marketParticipant.ref, 1, 1, tradable, uuid()))
      validOrders.foreach {
        validOrder => testMarket tell(validOrder, marketParticipant.ref)
      }

      Then("...it should notify the sender that the order has been accepted.")
      marketParticipant.expectNoMsg()

    }

    scenario("A MarketActor receives invalid Order messages.") {

      When("A MarketLike actor receives a invalid Order message...")
      val otherTradable = new TestTradable("APPL")
      val invalidOrders = List(MarketAskOrder(marketParticipant.ref, 1, 1, otherTradable, uuid()),
                               LimitBidOrder(marketParticipant.ref, 1, 1, 1, otherTradable, uuid()))
      invalidOrders.foreach {
        invalidOrder => testMarket tell(invalidOrder, marketParticipant.ref)
      }

      Then("...it should notify the sender that the order has been rejected.")
      marketParticipant.expectMsgAllClassOf[Rejected]()

    }
  }

  feature("A MarketActor should receive and process Cancel messages.") {

    val marketParticipant = TestProbe()

    // create a matching engine
    val matchingEngine = new BrokenMatchingEngine()
    val settlementMechanism = TestProbe()

    // create an initial tick
    val initialTick = Tick(1, 1, 1, 1, System.currentTimeMillis())
    val ticker = Agent(initialTick)(system.dispatcher)

    val tradable = TestTradable("GOOG")
    val marketProps = MarketActor.props(matchingEngine, settlementMechanism.ref, ticker, tradable)
    val testMarket = TestActorRef(marketProps)

    scenario("A MarketActor receives a Cancel message.") {

      Given("A MarketActor that has already received some existing orders...")
      val validOrders = List(LimitAskOrder(marketParticipant.ref, 1, 1, 1, tradable, uuid()),
        MarketBidOrder(marketParticipant.ref, 1, 1, tradable, uuid()))
      validOrders.foreach {
        validOrder => testMarket tell(validOrder, marketParticipant.ref)
      }

      When("A Cancel message arrives for one of the existing orders...")
      testMarket tell(Cancel(validOrders.head, 1, uuid()), marketParticipant.ref)

      Then("That order is removed from the underlying matchingEngine.")
      marketParticipant.expectMsgAllClassOf[Canceled]()

    }

  }

}
