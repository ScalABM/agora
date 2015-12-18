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

import java.util.UUID
import markets.engines.BrokenMatchingEngine
import markets.orders.limit.LimitAskOrder
import markets.tradables.TestTradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}


/** Test specification for a `ExchangeLike` actor.
  *
  * @note An `ExchangeLike` actor should directly receive `AskOrder` and `BidOrder` orders
  *       for a particular `Tradable` and then forward along all orders to a `MarketLike` actor
  *       for further processing. If necessary, the `ExchangeLike` actor should create a new
  *       `MarketActor` for the `Tradable`.
  */
class ExchangeActorSpec extends TestKit(ActorSystem("ExchangeActorSpec"))
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

  feature("An ExchangeActor should receive and process Order messages.") {

    val marketParticipant = TestProbe()
    val settlementMechanism = TestProbe()
    val tradable = TestTradable("GOOG")
    val exchangeProps = ExchangeActor.props(new BrokenMatchingEngine, settlementMechanism.ref)
    val testExchange = TestActorRef(exchangeProps)

    scenario("An ExchangeActor receives an Order message.") {

      When("An ExchangeActor receives an Order message...")
      val validOrder = LimitAskOrder(marketParticipant.ref, 1, 1, 1, tradable, uuid())
      testExchange tell(validOrder, marketParticipant.ref)

      Then("...it should create a child MarketActor and forward the order.")
      marketParticipant.expectMsgAllClassOf[Accepted]()

    }
  }

}
