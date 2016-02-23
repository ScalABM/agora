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

import markets.orders.limit.LimitOrderLike
import markets.tickers.Tick
import markets.tradables.{Tradable, TestTradable}
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpecLike}

import scala.collection.{immutable, mutable}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class LiquiditySupplierSpec extends TestKit(ActorSystem("LiquiditySupplierSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  feature("A LiquiditySupplier should be able to schedule SubmitLimitOrder messages.") {

    val tradable = TestTradable("GOOG")
    val market = TestProbe()
    val markets = mutable.Map[Tradable, ActorRef](tradable -> market.ref)
    val tickers = mutable.Map[Tradable, Agent[immutable.Seq[Tick]]](tradable -> Agent(immutable.Seq.empty[Tick]))

    scenario("A LiquiditySupplier schedules the future repeated submission of limit orders.") {

      When("a LiquitidySupplier schedules the repeated submission of limit orders...")
      val initialDelay = 0.25.seconds
      val interval = Some(0.5.seconds)
      val props = TestLiquiditySupplier.props(initialDelay, interval, markets, tickers)
      val liquiditySupplierRef = TestActorRef(props)

      Then("...the market should receive repeated limit orders.")

      val timeout = initialDelay + 1.25.second
      within(initialDelay, timeout) {  // @todo must be a better way to test this!
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
      }
    }
  }
}
