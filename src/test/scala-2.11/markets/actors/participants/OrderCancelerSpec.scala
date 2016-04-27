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
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import markets.tickers.Tick
import markets.{Accepted, Cancel, Canceled, Filled, MarketsTestKit}
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketBidOrder
import markets.actors.participants.strategies.{TestCancellationStrategy, TestTradingStrategy}
import markets.tradables.{TestTradable, Tradable}
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

  val initialTick = Tick(1, 1, 1, 1, timestamp())

  val prng = new Random(42)

  val tradable = TestTradable("GOOG")

  feature("An OrderCanceler should be able to add and remove outstanding orders.") {

    val market = TestProbe()
    val markets = Map[Tradable, ActorRef](tradable -> market.ref)
    val tickers = Map[Tradable, Agent[Tick]](tradable -> Agent(initialTick)(system.dispatcher))

    val tradingStrategy = TestTradingStrategy(Some(1), 1)
    val cancellationStrategy = new TestCancellationStrategy
    val props = TestOrderCanceler.props(markets, tickers, tradingStrategy, cancellationStrategy)
    val orderCancelerRef = TestActorRef[TestOrderCanceler](props)
    val orderCancelerActor = orderCancelerRef.underlyingActor

    scenario("An OrderCanceler receives a Filled message...") {

      Given("An OrderCanceler with outstanding orders...")
      val price = randomLimitPrice(prng)
      val quantity1 = randomQuantity(prng)
      val order1 = LimitAskOrder(orderCancelerRef, price, quantity1, timestamp(), tradable, uuid())
      val quantity2 = randomQuantity(prng)
      val order2 = MarketBidOrder(orderCancelerRef, quantity2, timestamp(), tradable, uuid())
      orderCancelerRef tell(Accepted(order1, timestamp(), uuid()), testActor)
      orderCancelerRef tell(Accepted(order2, timestamp(), uuid()), testActor)

      When("An OrderCanceler receives a Filled message with no residual order...")
      val filled = Filled(order1, None, timestamp(), uuid())
      orderCancelerRef tell(filled, testActor)

      Then("...it should remove the filled order.")
      orderCancelerActor.outstandingOrders.headOption should be(Some(order2))

      When("An OrderCanceler receives a Filled message with some residual order...")
      val residualQuantity = randomQuantity(prng, upper=quantity2)
      val(_, residualOrder) = order2.split(residualQuantity)
      val partialFilled = Filled(order2, Some(residualOrder), timestamp(), uuid())
      orderCancelerRef ! partialFilled

      Then("...it should remove the filled order and replace it with the residual order.")
      orderCancelerActor.outstandingOrders.headOption should be(Some(residualOrder))

    }

  }

  feature("A OrderCanceler should be able to process SubmitOrderCancellation messages.") {

    val market = TestProbe()
    val markets = Map[Tradable, ActorRef](tradable -> market.ref)
    val tickers = Map[Tradable, Agent[Tick]](tradable -> Agent(initialTick)(system.dispatcher))

    val tradingStrategy = TestTradingStrategy(Some(1), 1)
    val cancellationStrategy = new TestCancellationStrategy
    val props = TestOrderCanceler.props(markets, tickers, tradingStrategy, cancellationStrategy)

    scenario("An OrderCanceler with no outstanding orders receives SubmitOrderCancellation.") {

      Given("An OrderCanceler with no outstanding orders...")
      val orderCancelerRef = TestActorRef[TestOrderCanceler](props)

      When("an OrderCanceler with no outstanding orders receives SubmitOrderCancellation...")
      orderCancelerRef tell(SubmitOrderCancellation, testActor)

      Then("...no Cancel message should be generated.")
      market.expectNoMsg()

    }

    scenario("A OrderCanceler with outstanding orders receives SubmitOrderCancellation.") {

      Given("An OrderCanceler with some outstanding orders...")
      val orderCancelerRef = TestActorRef[TestOrderCanceler](props)
      val orderCancelerActor = orderCancelerRef.underlyingActor

      val order = LimitAskOrder(orderCancelerRef, 10, 100, timestamp(), tradable, uuid())
      orderCancelerActor.outstandingOrders += order

      When("an OrderCanceler receives SubmitOrderCancellation...")
      orderCancelerRef tell(SubmitOrderCancellation, testActor)

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
      val canceled = Canceled(order, 3, uuid())
      orderCancelerRef tell(canceled, testActor)

      Then("...it should remove the canceled order from its outstanding orders.")
      orderCancelerActor.outstandingOrders.headOption should be(None)

    }
  }

}
