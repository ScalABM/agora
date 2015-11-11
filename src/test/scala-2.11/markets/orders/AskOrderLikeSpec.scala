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
package markets.orders

import akka.actor.ActorSystem
import akka.testkit.TestKit
import markets.orders.orderings.PriceTimeAskOrdering
import markets.tradables.Tradable
import org.scalatest.{GivenWhenThen, FeatureSpecLike, Matchers}


class AskOrderLikeSpec extends TestKit(ActorSystem("AskOrderLikeSpec")) with
  FeatureSpecLike with
  GivenWhenThen with
  Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  /** Stub Tradable object for testing purposes. */
  case class TestTradable(ticker: String) extends Tradable

  feature("Ordering logic for AskOrderLike.") {

    scenario("asdfasdf") {
      val askOrders = List[AskOrderLike](LimitAskOrder(testActor, 100, 1, 1, TestTradable("GOOG")),
                                         LimitAskOrder(testActor, 10, 1, 1, TestTradable("GOOG")),
                                         MarketAskOrder(testActor, 1, 1, TestTradable("GOOG")))

      assert(askOrders.sorted(PriceTimeAskOrdering) === askOrders.reverse)
    }

  }

}