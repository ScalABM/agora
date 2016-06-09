/*
Copyright 2016 ScalABM

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
package markets.engines

import akka.actor.ActorSystem
import akka.testkit.TestKit

import markets.engines.immutable.ImmutableTreeSetCDAMatchingEngine
import markets.engines.mutable.MutableTreeSetCDAMatchingEngine
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import org.scalameter.api.Gen


/** Regression test suite for MutableTreeSetCDAMatchingEngine. */
object CDAMatchingEngineMicroBench extends TreeSetCDAMatchingEngineMicroBench {

  val testKit = new TestKit(ActorSystem())

  /* Setup the matching engine MutableTreeSetCDAMatchingEngine. */
  val askOrdering = AskPriceTimeOrdering
  val bidOrdering = BidPriceTimeOrdering
  val referencePrice = config.getLong("matching-engines.reference-price")
  val immutableMatchingEngine = ImmutableTreeSetCDAMatchingEngine(askOrdering, bidOrdering, referencePrice)
  val mutableMatchingEngine = MutableTreeSetCDAMatchingEngine(askOrdering, bidOrdering, referencePrice)

  /* Generate a range of numbers of orders to use when generating input data. */
  val numbersOfOrders = {
    val hop = config.getInt("matching-engines.input-data.number-orders.hop")
    val upto = config.getInt("matching-engines.input-data.number-orders.upto")
    val from = config.getInt("matching-engines.input-data.number-orders.from")
    Gen.range("Number of Orders")(hop, upto, from)
  }

  /* Input data is a collection of sequences of randomly generated orders. */
  val inputData = for { number <- numbersOfOrders } yield generateOrders(number, tradable)

  performance of "MutableTreeSetCDAMatchingEngine" in {
    measure method "findMatch" in {
      using(inputData) in {
        orders => orders.map(mutableMatchingEngine.findMatch)
      }
    }
    testKit.system.terminate()  // Don't forget to shutdown the ActorSystem!
  }

  performance of "ImmutableTreeSetCDAMatchingEngine" in {
    measure method "findMatch" in {
      using(inputData) in {
        orders => orders.map(immutableMatchingEngine.findMatch)
      }
    }
    testKit.system.terminate()  // Don't forget to shutdown the ActorSystem!
  }

}
