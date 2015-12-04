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
import akka.testkit.{TestActorRef, TestKit}

import java.util.UUID

import markets.{Canceled, Accepted}
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.tradables.Security
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}


/** Test specification for a `MarketLike` actor.
  *
  * @note A `MarketLike` actor should directly receive `AskOrder` and `BidOrder` orders
  *       for a particular `Tradable` (filtering out any invalid orders) and then forward along
  *       all valid orders to a `ClearingMechanismLike` actor for further processing.
  */
class MarketParticipantLikeSpec extends TestKit(ActorSystem("MarketParticipantLikeSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  def uuid: UUID = {
    UUID.randomUUID()
  }

  feature("A MarketParticipantLike actor should be able to add and remove outstanding orders.") {

    val marketParticipant = TestActorRef(new TestMarketParticipant)

    val tradable = Security("GOOG")
    val order = LimitAskOrder(marketParticipant, 10, 100, 1, tradable, uuid)

    scenario("A MarketParticipantLike actor receives an Accepted message...") {

      When("A MarketParticipantLike actor receives an Accepted message...")
      val accepted = Accepted(order, 2, uuid)
      marketParticipant ! accepted

      Then("...it should add the accepted orders UUID to its outstanding orders.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.outstandingOrders.headOption should be(Some(order.uuid))

    }

    scenario("A MarketParticipantLike actor receives a Canceled message...") {

      When("A MarketParticipantLike actor receives a Canceled message...")
      val canceled = Canceled(order, 3, uuid)
      marketParticipant ! canceled

      Then("...it should remove the canceled orders UUID from its outstanding orders.")
      val marketParticipantActor = marketParticipant.underlyingActor
      marketParticipantActor.outstandingOrders.headOption should be(None)

    }

  }

}
