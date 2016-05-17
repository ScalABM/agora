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
package markets.actors.participants

import akka.actor.{ActorRef, ActorSystem}
import akka.agent.Agent
import akka.testkit.{TestKit, TestProbe}

import markets.MarketsTestKit
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.actors.participants.strategies.ConstantOrderIssuingStrategy
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}


class OrderIssuerSpec extends TestKit(ActorSystem("OrderIssuerSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  val tradable = Tradable("GOOG")

  val market = TestProbe()

  val initialTick = Tick(1, 1, 1, 1, timestamp())
  val ticker = Agent(initialTick)(system.dispatcher)

  feature("An OrderIssuer should be able to issue limit orders.") {

    val askOrderIssuingStrategy = ConstantOrderIssuingStrategy[AskOrder](Some(2), 1, Some(tradable))
    val bidOrderIssuingStrategy = ConstantOrderIssuingStrategy[BidOrder](Some(1), 1, Some(tradable))
    val props = TestOrderIssuer.props(askOrderIssuingStrategy, bidOrderIssuingStrategy)
    val orderIssuer = system.actorOf(props)

    orderIssuer ! Add(tradable, market.ref, ticker)

    scenario("A OrderIssuer receives a IssueAskOrder message.") {

      When("an OrderIssuer receives a IssueAskOrder message...")
      orderIssuer.tell(IssueAskOrder, testActor)

      Then("...the market should receive an ask order.")
      market.expectMsgAnyClassOf(classOf[LimitAskOrder])

    }

    scenario("A OrderIssuer receives a IssueBidOrder message.") {

      When("an OrderIssuer receives a IssueBidOrder message...")
      orderIssuer.tell(IssueBidOrder, testActor)

      Then("...the market should receive an bid order.")
      market.expectMsgAnyClassOf(classOf[LimitBidOrder])
    }

  }

  feature("An OrderIssuer should be able to issue market orders.") {

    val askOrderIssuingStrategy = ConstantOrderIssuingStrategy[AskOrder](None, 1, Some(tradable))
    val bidOrderIssuingStrategy = ConstantOrderIssuingStrategy[BidOrder](None, 1, Some(tradable))
    val props = TestOrderIssuer.props(askOrderIssuingStrategy, bidOrderIssuingStrategy)
    val orderIssuer = system.actorOf(props)

    orderIssuer ! Add(tradable, market.ref, ticker)

    scenario("A OrderIssuer receives a IssueAskOrder message.") {

      When("an OrderIssuer receives a IssueAskOrder message...")
      orderIssuer.tell(IssueAskOrder, testActor)

      Then("...the market should receive an ask order.")
      market.expectMsgAnyClassOf(classOf[MarketAskOrder])

    }

    scenario("A OrderIssuer receives a IssueBidOrder message.") {

      When("an OrderIssuer receives a IssueBidOrder message...")
      orderIssuer.tell(IssueBidOrder, testActor)

      Then("...the market should receive an bid order.")
      market.expectMsgAnyClassOf(classOf[MarketBidOrder])
    }
  }

}
