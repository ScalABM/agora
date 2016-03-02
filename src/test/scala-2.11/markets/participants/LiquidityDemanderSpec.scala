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
    val timestamp = System.currentTimeMillis()
    val tickers = mutable.Map[Tradable, Agent[Tick]](tradable -> Agent(Tick(1, 1, 1, 1, timestamp)))

    scenario("A LiquidityDemander schedules the future repeated submission of market orders.") {
      
      When("a LiquidityDemander schedules the repeated submission of market orders...")
      val initialDelay = 0.25.seconds
      val interval = Some(0.5.seconds)
      val props = TestLiquidityDemander.props(initialDelay, interval, markets, tickers)
      val liquidityDemanderRef = TestActorRef(props)

      Then("...the market should receive repeated market orders.")

      val timeout = initialDelay + 1.25.second
      within(initialDelay, timeout) {  // @todo must be a better way to test this!
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
      }
    }
  }
}
