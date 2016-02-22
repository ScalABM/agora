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

import markets.orders.market.MarketOrderLike
import markets.tickers.Tick
import markets.tradables.{Tradable, TestTradable}
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpecLike}

import scala.collection.{immutable, mutable}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class LiquidityDemanderSpec extends TestKit(ActorSystem("LiquidityDemanderSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  feature("A LiquidityDemander should be able to schedule SubmitMarketOrder messages.") {

    val tradable = TestTradable("GOOG")
    val market = TestProbe()
    val markets = mutable.Map[Tradable, ActorRef](tradable -> market.ref)
    val tickers = mutable.Map[Tradable, Agent[immutable.Seq[Tick]]](tradable -> Agent(immutable.Seq.empty[Tick]))

    scenario("A LiquidityDemander schedules the future submission of a single market order.") {
      val initialDelay = 10.millis
      val props = TestLiquidityDemander.props(initialDelay, None, markets, tickers)
      val liquidityDemanderRef = TestActorRef(props)

      Then("...the market should receive a single market order.")

      val timeout = initialDelay + 50.millis // @todo is this the best way to test?
      within(initialDelay, timeout) {
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
      }
    }

    scenario("A LiquidityDemander schedules the future repeated submission of market orders.") {
      
      When("a LiquitidySupplier schedules the repeated submission of market orders...")
      val initialDelay = 10.millis
      val interval = Some(5.millis)
      val props = TestLiquidityDemander.props(initialDelay, interval, markets, tickers)
      val liquidityDemanderRef = TestActorRef(props)

      Then("...the market should receive repeated market orders.")

      val timeout = initialDelay + 50.millis
      within(initialDelay, timeout) {  // @todo must be a better way to test this!
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
      }
    }
  }
}
