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
package markets.matching

import markets.mutable.orderbooks.OrderBook
import markets.tradables.orders.ask.{AskOrder, LimitAskOrder}
import markets.tradables.orders.bid.LimitBidOrder
import markets.tradables.Security

import org.scalatest.{FeatureSpec, Matchers}

import scala.util.Random


/** Class testing basic functionality of the `FindFirstMatchingFunction`. */
class FindFirstMatchingFunctionSpec extends FeatureSpec with Matchers {

  import markets.RandomOrderGenerator._

  val prng = new Random()

  val tradable = Security(uuid())

  feature("FindFirstMatchingFunction should return None if orderBook contains no acceptable orders.") {

    scenario("Given an empty orderBook, FindFirstMatchingMechanism should return None.") {
      val order = LimitBidOrder(uuid(), 10, 1, 1, tradable, uuid())
      val orderBook = OrderBook[AskOrder](tradable)
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(order, orderBook)
      result should be(None)
    }

    scenario("Given an orderBook with no acceptable orders, FindFirstMatchingMechanism should return None.") {

      val orderBook = OrderBook[AskOrder](tradable)
      val askOrder = LimitAskOrder(uuid(), 15, 1, 1, tradable, uuid())
      orderBook.add(askOrder)

      val bidOrder = LimitBidOrder(uuid(), 10, 1, 1, tradable, uuid())
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(bidOrder, orderBook)
      result should be(None)
    }

  }

  feature("FindFirstMatchingFunction should find the an acceptable order when it exists.") {

    scenario("Given an orderBook with a single acceptable order, FindFirstMatchingMechanism should return that order.") {

      val orderBook = OrderBook[AskOrder](tradable)
      val askOrder = LimitAskOrder(uuid(), 9, 1, 1, tradable, uuid())
      orderBook.add(askOrder)

      val bidOrder = LimitBidOrder(uuid(), 10, 1, 1, tradable, uuid())
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(bidOrder, orderBook)
      result should be(Some(bidOrder, askOrder))

    }

    scenario("Given an orderBook with a unique acceptable order, FindFirstMatchingMechanism should return that order.") {

      val bidPrice = 10
      val bidOrder = LimitBidOrder(uuid(), bidPrice, 1, 1, tradable, uuid())

      // create an order book and add a single acceptable ask order
      val orderBook = OrderBook[AskOrder](tradable)
      val matchingAskOrder = LimitAskOrder(uuid(), 9, 1, 1, tradable, uuid())
      orderBook.add(matchingAskOrder)

      // add a bunch of unacceptable ask orders
      val numberOrders = 100
      for (i <- 1 to numberOrders) {
        val askOrder = randomAskOrder(prng, marketOrderProbability=0.0, minimumPrice=bidPrice, tradable=tradable)
        orderBook.add(askOrder)
      }

      // find the matching order
      val matchingFunction = new FindFirstMatchingFunction[AskOrder, LimitBidOrder]
      val result = matchingFunction(bidOrder, orderBook)
      result should be(Some(bidOrder, matchingAskOrder))

    }

  }

}
