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

import akka.actor.{ActorRef, ActorSystem}
import akka.agent.Agent
import akka.testkit.{TestProbe, TestActorRef, TestKit}

import java.util.UUID

import markets.{Cancel, Canceled}
import markets.orders.limit.LimitAskOrder
import markets.tickers.Tick
import markets.tradables.{Tradable, TestTradable}
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpecLike}

import scala.collection.{immutable, mutable}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class OrderCancelerSpec extends TestKit(ActorSystem("OrderCancelerSpec"))
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

  feature("A OrderCanceler should be able to schedule SubmitOrderCancellation messages.") {

    val tradable = TestTradable("GOOG")
    val market = TestProbe()
    val markets = mutable.Map[Tradable, ActorRef](tradable -> market.ref)
    val tickers = mutable.Map[Tradable, Agent[immutable.Seq[Tick]]](tradable -> Agent(immutable.Seq.empty[Tick]))

    scenario("A OrderCanceler with no outstanding orders schedules an order cancellation.") {

      When("An OrderCanceler has no outstanding orders...")
      val initialDelay = 10.millis
      val props = TestOrderCanceler.props(initialDelay, markets, tickers)
      val orderCancelerRef = TestActorRef[OrderCanceler](props)

      Then("...then no order cancellation should be generated.")
      market.expectNoMsg()

    }

    scenario("A OrderCanceler with outstanding orders schedules an order cancellation.") {

      When("An OrderCanceler has some outstanding orders...")
      val initialDelay = 100.millis
      val props = TestOrderCanceler.props(initialDelay, markets, tickers)
      val orderCancelerRef = TestActorRef[OrderCanceler](props)
      val orderCancelerActor = orderCancelerRef.underlyingActor

      val order = LimitAskOrder(orderCancelerRef, 10, 100, timestamp(), tradable, uuid())
      orderCancelerActor.outstandingOrders += order

      Then("...the market should receive a Cancel message.")
      val timeout = initialDelay + 50.millis  // @todo is this the best way to test?
      within(initialDelay, timeout) {
        market.expectMsgAnyClassOf(classOf[Cancel])
      }
    }

    scenario("An OrderCanceler actor receives a Canceled message...") {

      Given("An OrderCanceler has some outstanding orders...")
      val initialDelay = 100.millis
      val props = TestOrderCanceler.props(initialDelay, markets, tickers)
      val orderCancelerRef = TestActorRef[OrderCanceler](props)
      val orderCancelerActor = orderCancelerRef.underlyingActor

      val order = LimitAskOrder(orderCancelerRef, 10, 100, timestamp(), tradable, uuid())
      orderCancelerActor.outstandingOrders += order

      When("An OrderCanceler actor receives a Canceled message...")
      val canceled = Canceled(order, 3, uuid())
      orderCancelerRef ! canceled

      Then("...it should remove the canceled order from its outstanding orders.")
      orderCancelerActor.outstandingOrders.headOption should be(None)

    }
  }
}
