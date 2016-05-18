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
import markets.actors.participants.strategies.TestOrderIssuingStrategy
import markets.actors.{Accepted, Filled}
import markets.orders.limit.LimitAskOrder
import markets.orders.market.MarketBidOrder
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable
import org.scalatest.{FeatureSpecLike, GivenWhenThen, Matchers}

import scala.util.Random


/** Test specification for any actor mixing in the `OrderTracker` trait. */
class OrderTrackerSpec extends TestKit(ActorSystem("OrderTrackerSpec"))
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

  feature("An OrderTracker should be able to add and remove outstanding orders.") {

    val askOrderIssuingStrategy = TestOrderIssuingStrategy[AskOrder](Some(2), 1, tradable)
    val bidOrderIssuingStrategy = TestOrderIssuingStrategy[BidOrder](Some(1), 1, tradable)
    val props = TestOrderTracker.props(askOrderIssuingStrategy, bidOrderIssuingStrategy)
    val orderTrackerRef = TestActorRef[TestOrderTracker](props)
    val orderTrackerActor = orderTrackerRef.underlyingActor

    orderTrackerRef ! Add(tradable, market.ref, ticker)

    scenario("An OrderTracker receives a Filled message...") {

      Given("An OrderTracker with outstanding orders...")
      val price = randomLimitPrice(prng)
      val quantity1 = randomQuantity(prng)
      val order1 = LimitAskOrder(orderTrackerRef, price, quantity1, timestamp(), tradable, uuid())
      val quantity2 = randomQuantity(prng)
      val order2 = MarketBidOrder(orderTrackerRef, quantity2, timestamp(), tradable, uuid())
      orderTrackerRef tell(Accepted(order1), testActor)
      orderTrackerRef tell(Accepted(order2), testActor)

      When("An OrderTracker receives a Filled message with no residual order...")
      val filled = Filled(order1, None)
      orderTrackerRef tell(filled, testActor)

      Then("...it should remove the filled order.")
      orderTrackerActor.outstandingOrders.headOption should be(Some(order2))

      When("An OrderTracker receives a Filled message with some residual order...")
      val residualQuantity = randomQuantity(prng, upper=quantity2)
      val(_, residualOrder) = order2.split(residualQuantity)
      val partialFilled = Filled(order2, Some(residualOrder))
      orderTrackerRef ! partialFilled

      Then("...it should remove the filled order and replace it with the residual order.")
      orderTrackerActor.outstandingOrders.headOption should be(Some(residualOrder))

    }

  }
  
}
