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
package markets.exchanges

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import markets.OrderAccepted
import markets.clearing.engines.BrokenMatchingEngine
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.Tradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}


/** Test specification for a `ExchangeLike` actor.
  *
  * @note A `MarketLike` actor should directly receive `AskOrder` and `BidOrder` orders
  *       for a particular `Tradable` (filtering out any invalid orders) and then forward along
  *       all valid orders to a `ClearingMechanismLike` actor for further processing.
  */
class ExchangeActorSpec extends TestKit(ActorSystem("ExchangeActorSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  /** Stub Tradable object for testing purposes. */
  case class TestTradable(ticker: String) extends Tradable

  feature("An ExchangeActor should receive and process Order messages.") {

    val marketParticipant = TestProbe()
    val settlementMechanism = TestProbe()
    val tradable = new TestTradable("GOOG")
    val exchangeProps = ExchangeActor.props(new BrokenMatchingEngine, settlementMechanism.ref)
    val testExchange = TestActorRef(exchangeProps)

    scenario("An ExchangeActor receives an Order message.") {

      When("An ExchangeActor receives an Order message...")
      val validOrder = LimitAskOrder(marketParticipant.ref, 1, 1, 1, tradable)
      testExchange tell(validOrder, marketParticipant.ref)

      Then("...it should create a child MarketActor and forward the order.")
      marketParticipant.expectMsgAllOf(OrderAccepted)

    }
  }
}
