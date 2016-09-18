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
package markets.parallel.mutable

import markets.MarketsTestKit
import markets.orders.AskOrder
import markets.orders.limit.LimitOrder
import markets.parallel.mutable.orderbooks.OrderBook
import org.scalameter.api._
import org.scalameter.{Bench, Gen}

import scala.util.Random


/** Performance tests for the `OrderBook` class. */
object OrderBookMicroBenchmark extends Bench.OnlineRegressionReport with MarketsTestKit {

  val prng = new Random(42)

  val sizes = Gen.exponential("Number of existing orders")(factor=10, until=1000000, from=10)

  /** Generates a collection of OrderBooks of increasing size. */
  val orderBooks = for { size <- sizes } yield {
    val orderBook = OrderBook[AskOrder](validTradable)
    val orders = for (i <- 1 to size) yield randomAskOrder(tradable = validTradable)
    orders.foreach( order => orderBook.add(order) )
    orderBook
  }

  performance of "OrderBook" config (
    reports.resultDir -> "target/benchmarks/markets/engines/orderbooks/OrderBook",
    exec.benchRuns -> 200,
    exec.independentSamples -> 20,
    exec.jvmflags -> List("-Xmx2G")
    ) in {

    /** Adding an `Order` to an `OrderBook` should be an `O(1)` operation. */
    measure method "add" in {
      using(orderBooks) in {
        orderBook =>
          val newOrder = randomAskOrder(tradable=validTradable)
          orderBook.add(newOrder)
      }
    }

    /** Filtering an `OrderBook` should be an `O(n)` operation. */
    measure method "filter" in {
      using(orderBooks) in {
        orderBook => orderBook.filter(order => order.isInstanceOf[LimitOrder])
      }
    }

    /** Finding an `Order` in an `OrderBook` should be an `O(n)` operation. */
    measure method "find" in {
      using(orderBooks) in {
        orderBook => orderBook.find(order => order.isInstanceOf[LimitOrder])
      }
    }

    /** Removing an `Order` from an `OrderBook` should be an `O(1)` operation. */
    measure method "remove(order)" in {
      using(orderBooks) in {
        orderBook =>
          val (uuid, _) = orderBook.existingOrders.head
          orderBook.remove(uuid)
      }
    }

    /** Removing the head `Order` from an `OrderBook` should be an `O(1)` operation. */
    measure method "remove()" in {
      using(orderBooks) in {
        orderBook => orderBook.remove()
      }
    }

  }

}
