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
package markets

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import markets.clearing.engines.BrokenMatchingEngine
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.Tradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}


/** Test specification for a `MarketLike` actor.
  *
  * @note A `MarketLike` actor should directly receive `AskOrderLike` and `BidOrderLike` orders
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

  /** Stub Tradable object for testing purposes. */
  case class TestTradable(ticker: String) extends Tradable

  feature("A MarketActor should receive and process OrderLike messages.") {

    val marketParticipant = TestProbe()
    val settlementMechanism = TestProbe()
    val tradable = new TestTradable("GOOG")
    val testMarket = TestActorRef(MarketActor(new BrokenMatchingEngine(), settlementMechanism.ref,
      tradable))

    scenario("A MarketActor receives valid OrderLike messages.") {

      When("A MarketActor receives a valid OrderLike message...")
      val validOrders = List(LimitAskOrder(marketParticipant.ref, 1, 1, 1, tradable),
                             MarketBidOrder(marketParticipant.ref, 1, 1, tradable))
      validOrders.foreach {
        validOrder => testMarket tell(validOrder, marketParticipant.ref)
      }

      Then("...it should notify the sender that the order has been accepted.")
      marketParticipant.expectMsgAllOf(OrderAccepted, OrderAccepted)

    }

    scenario("A MarketActor receives invalid OrderLike messages.") {

      When("A MarketLike actor receives a invalid OrderLike message...")
      val otherTradable = new TestTradable("APPL")
      val invalidOrders = List(MarketAskOrder(marketParticipant.ref, 1, 1, otherTradable),
                               LimitBidOrder(marketParticipant.ref, 1, 1, 1, otherTradable))
      invalidOrders.foreach {
        invalidOrder => testMarket tell(invalidOrder, marketParticipant.ref)
      }

      Then("...it should notify the sender that the order has been rejected.")
      marketParticipant.expectMsgAllOf(OrderRejected, OrderRejected)

    }
  }
}
