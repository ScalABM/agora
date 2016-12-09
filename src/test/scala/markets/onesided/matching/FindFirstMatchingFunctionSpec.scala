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
package markets.onesided.matching

import markets.OrderGenerator
import markets.orderbooks.mutable.OrderBook
import markets.tradables.TestTradable
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.LimitBidOrder
import org.scalatest.{FeatureSpec, Matchers}


/** Class testing basic functionality of the `FindFirstMatchingFunction`. */
class FindFirstMatchingFunctionSpec extends FeatureSpec with Matchers with OrderGenerator {

  val tradable = TestTradable()

  feature("FindFirstMatchingFunction should return None if orderBook contains no acceptable orders.") {

    scenario("Given an empty orderBook, FindFirstMatchingFunction should return None.") {
      val order = orderGenerator.nextLimitBidOrder(None, tradable)
      val orderBook = OrderBook[AskOrder](tradable)
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(order, orderBook)
      result should be(None)
    }

    scenario("Given an orderBook with no acceptable orders, FindFirstMatchingFunction should return None.") {

      val orderBook = OrderBook[AskOrder](tradable)
      val askPrice = 15
      val askOrder = orderGenerator.nextLimitAskOrder(askPrice, None, tradable)
      orderBook.add(askOrder)

      val bidPrice = 10
      val bidOrder = orderGenerator.nextLimitBidOrder(bidPrice, None, tradable)
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(bidOrder, orderBook)
      result should be(None)
    }

  }

  feature("FindFirstMatchingFunction should find the an acceptable order when it exists.") {

    scenario("Given an orderBook with a single acceptable order, FindFirstMatchingFunction should return that order.") {

      val orderBook = OrderBook[AskOrder](tradable)
      val askPrice = 9
      val askOrder = orderGenerator.nextLimitAskOrder(askPrice, None, tradable)
      orderBook.add(askOrder)

      val bidPrice = 10
      val bidOrder = orderGenerator.nextLimitBidOrder(bidPrice, None, tradable)
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(bidOrder, orderBook)
      result should be(Some(bidOrder, askOrder))

    }

    scenario("Given an orderBook with a unique acceptable order, FindFirstMatchingFunction should return that order.") {

      val bidPrice = 10
      val bidOrder = orderGenerator.nextLimitBidOrder(bidPrice, None, tradable)

      // create an order book and add a single acceptable ask order
      val orderBook = OrderBook[AskOrder](tradable)
      val askPrice = 9
      val matchingAskOrder = orderGenerator.nextLimitAskOrder(askPrice, None, tradable)
      orderBook.add(matchingAskOrder)

      // add a bunch of unacceptable ask orders
      val numberOrders = 100
      for (i <- 1 to numberOrders) {
        val askOrder = orderGenerator.nextLimitAskOrder(bidPrice + i, None, tradable)  // prices must be sufficiently high!
        orderBook.add(askOrder)
      }

      // find the matching order
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(bidOrder, orderBook)
      result should be(Some(bidOrder, matchingAskOrder))

    }

  }

}
