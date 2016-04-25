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
import akka.testkit.{TestKit, TestProbe}

import markets.MarketsTestKit
import markets.orders.{AskOrder, BidOrder}
import markets.participants.strategies.TestTradingStrategy
import markets.tickers.Tick
import markets.tradables.{TestTradable, Tradable}
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global


class OrderIssuerSpec extends TestKit(ActorSystem("OrderIssuerSpec"))
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
  val markets = mutable.Map[Tradable, ActorRef](tradable -> market.ref)

  val initialTick = Tick(1, 1, 1, 1, timestamp())
  val tickers = mutable.Map[Tradable, Agent[Tick]](tradable -> Agent(initialTick))

  feature("An OrderIssuer should be able to issue ask orders.") {

    val tradingStrategy = new TestTradingStrategy(Some(1), 1)
    val props = TestOrderIssuer.props(markets, tickers, tradingStrategy)
    val orderIssuer = system.actorOf(props)

    scenario("A OrderIssuer receives a SubmitAskOrder message.") {

      When("an OrderIssuer receives a SubmitAskOrder message...")
      orderIssuer.tell(SubmitAskOrder, testActor)

      Then("...the market should receive an ask order.")
      market.expectMsgAnyClassOf(classOf[AskOrder])

    }
  }

  feature("An OrderIssuer should be able to issue bid orders.") {

    val tradingStrategy = new TestTradingStrategy(Some(1), 1)
    val props = TestOrderIssuer.props(markets, tickers, tradingStrategy)
    val orderIssuer = system.actorOf(props)

    scenario("A OrderIssuer receives a SubmitBidOrder message.") {

      When("an OrderIssuer receives a SubmitBidOrder message...")
      orderIssuer.tell(SubmitBidOrder, testActor)

      Then("...the market should receive an bid order.")
      market.expectMsgAnyClassOf(classOf[BidOrder])

    }
  }

}
