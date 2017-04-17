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
import akka.testkit.{TestActorRef, TestProbe, TestKit}
import markets.orders.{BidOrderLike, AskOrderLike}
import markets.tradables.Tradable
import org.scalatest.{FeatureSpecLike, Matchers, GivenWhenThen}


/** Test specification for a `MarketLike` actor.
  *
  * @note A `MarketLike` actor should directly receive `AskOrderLike` and `BidOrderLike` orders for a particular
  *       `Tradable` (filtering out any invalid orders) and then forward along all valid orders to a
  *       `ClearingMechanismLike` actor for further processing.
  */
class MarketLikeSpec extends TestKit(ActorSystem("MarketLikeSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Stub Tradable object for testing purposes. */
  class TestTradable extends Tradable

  /** Stub AskOrderLike object for testing purposes. */
  case class TestAskOrderLike(tradable: Tradable) extends AskOrderLike

  /** Stub BidOrderLike object for testing purposes. */
  case class TestBidOrderLike(tradable: Tradable) extends BidOrderLike

  feature("A MarketLike actor should receive and process OrderLike messages.") {

    val marketParticipant = TestProbe()
    val clearingMechanism = TestProbe()
    val settlementMechanism = testActor
    val tradable = new TestTradable()
    val testMarket = TestActorRef(TestMarket(clearingMechanism.ref, settlementMechanism, tradable))

    scenario("A MarketLike actor receives valid OrderLike messages.") {

      When("A MarketLike actor receives a valid OrderLike message...")
      val validOrders = List(TestAskOrderLike(tradable), TestBidOrderLike(tradable))
      validOrders.foreach {
        validOrder => testMarket tell(validOrder, marketParticipant.ref)
      }

      Then("...it should forward it to its clearing mechanism...")
      clearingMechanism.expectMsgAllOf(validOrders.head, validOrders(1))

      Then("...it should notify the sender that the order has been accepted.")
      marketParticipant.expectMsgAllOf(OrderAccepted, OrderAccepted)
    }

    scenario("A MarketLike actor receives invalid OrderLike messages.") {

      When("A MarketLike actor receives a invalid OrderLike message...")
      val otherTradable = new TestTradable()
      val invalidOrders = List(TestAskOrderLike(otherTradable), TestBidOrderLike(otherTradable))
      invalidOrders.foreach {
        invalidOrder => testMarket tell(invalidOrder, marketParticipant.ref)
      }

      Then("...it should notify the sender that the order has been rejected.")
      marketParticipant.expectMsgAllOf(OrderRejected, OrderRejected)
      clearingMechanism.expectNoMsg()

    }
  }
}
