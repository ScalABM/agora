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

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import markets.MarketsTestKit
import markets.tickers.Tick
import markets.actors.{Cancel, Canceled}
import markets.orders.limit.LimitAskOrder
import markets.actors.participants.strategies.{TestOrderCancellationStrategy, TestOrderIssuingStrategy}
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.util.Random


/** Test specification for any actor mixing in the `OrderCanceler` trait. */
class OrderCancelerSpec extends TestKit(ActorSystem("OrderCancelerSpec"))
  with MarketsTestKit
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  val prng = new Random(42)

  val tradable = Tradable("GOOG")

  val market = TestProbe()

  val initialTick = Tick(1, 1, 1, 1, timestamp())

  val ticker = Agent(initialTick)(system.dispatcher)

  feature("A OrderCanceler should be able to process IssueOrderCancellation messages.") {

    val askOrderIssuingStrategy = TestOrderIssuingStrategy[AskOrder](Some(2), 1, tradable)
    val bidOrderIssuingStrategy = TestOrderIssuingStrategy[BidOrder](Some(1), 1, tradable)
    val cancellationStrategy = new TestOrderCancellationStrategy
    val props = TestOrderCanceler.props(askOrderIssuingStrategy, bidOrderIssuingStrategy, cancellationStrategy)
    val orderCancelerRef = TestActorRef[TestOrderCanceler](props)
    val orderCancelerActor = orderCancelerRef.underlyingActor

    orderCancelerRef ! Add(tradable, market.ref, ticker)

    scenario("An OrderCanceler with no outstanding orders receives IssueOrderCancellation.") {

      Given("An OrderCanceler with no outstanding orders...")
      val orderCancelerRef = TestActorRef[TestOrderCanceler](props)

      When("an OrderCanceler with no outstanding orders receives IssueOrderCancellation...")
      orderCancelerRef tell(IssueOrderCancellation, testActor)

      Then("...no Cancel message should be generated.")
      market.expectNoMsg()

    }

    scenario("A OrderCanceler with outstanding orders receives IssueOrderCancellation.") {

      Given("An OrderCanceler with some outstanding orders...")

      val order = LimitAskOrder(orderCancelerRef, 10, 100, timestamp(), tradable, uuid())
      orderCancelerActor.outstandingOrders += order

      When("an OrderCanceler receives IssueOrderCancellation...")
      orderCancelerRef tell(IssueOrderCancellation, testActor)

      Then("...the market should receive a Cancel message.")
      market.expectMsgClass(classOf[Cancel])

    }

    scenario("An OrderCanceler actor receives a Canceled message...") {

      Given("An OrderCanceler with some outstanding orders...")
      val orderCancelerRef = TestActorRef[TestOrderCanceler](props)
      val orderCancelerActor = orderCancelerRef.underlyingActor

      val order = LimitAskOrder(orderCancelerRef, 10, 100, timestamp(), tradable, uuid())
      orderCancelerActor.outstandingOrders += order

      When("An OrderCanceler actor receives a Canceled message...")
      val canceled = Canceled(order)
      orderCancelerRef tell(canceled, testActor)

      Then("...it should remove the canceled order from its outstanding orders.")
      orderCancelerActor.outstandingOrders.headOption should be(None)

    }
  }

}
