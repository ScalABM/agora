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
package markets.performance

import akka.actor.ActorSystem
import akka.testkit.TestKit

import markets.engines.CDAMatchingEngine
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.tradables.Tradable
import org.scalameter.api._


object CDAMatchingEngineMicroBench extends MatchingEngineMicroBench {

  val testKit = new TestKit(ActorSystem())

  val matchingEngine = CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, 1)

  val inputData = for {
    numberOrders <- Gen.range("Number of Orders")(1000, 10000, 1000)
  } yield generateOrders(numberOrders, tradable)

  performance of "CDAMatchingEngine" in {
    measure method "findMatch" in {
      using(inputData) in {
        orders => orders.map(matchingEngine.findMatch)
      }
    }
    testKit.system.terminate()
  }

}
