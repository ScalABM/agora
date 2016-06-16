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
package markets.engines.orderbooks

import markets.MarketsTestKit
import markets.orders.AskOrder
import markets.orders.orderings.ask.AskPriceOrdering
import org.scalameter.{Bench, Gen}

import scala.util.Random


/** Performance tests for the `SortedOrderBook` class. */
object SortedOrderBookMicroBenchmark extends Bench.OnlineRegressionReport with MarketsTestKit {

  val prng = new Random()

  val sizes = Gen.exponential("Number of existing orders")(factor = 10, from = 10, until = 1000000)

  /** Generates a collection of OrderBooks of increasing size. */
  val orderBooks = for { size <- sizes } yield {
    val orderBook = SortedOrderBook[AskOrder](validTradable)(AskPriceOrdering)
    val orders = for (i <- 1 to size) yield randomAskOrder(tradable = validTradable)
    orders.foreach( order => orderBook.add(order) )
    orderBook
  }

  performance of "SortedOrderBook" in {

    /** Adding an `Order` to a `SortedOrderBook` should be an `O(log n)` operation. */
    measure method "add" in {
      using(orderBooks) in {
        orderBook =>
          val newOrder = randomAskOrder(tradable=validTradable)
          orderBook.add(newOrder)
      }
    }

    /** Removing an `Order` from a `SortedOrderBook` should be an `O(log n)` operation. */
    measure method "remove" in {
      using(orderBooks) in {
        orderBook =>
          val (uuid, _) = orderBook.existingOrders.head
          orderBook.remove(uuid)
      }
    }

  }

}
