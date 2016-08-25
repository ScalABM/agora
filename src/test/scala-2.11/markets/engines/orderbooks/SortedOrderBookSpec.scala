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

import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable

import scala.util.Random


class SortedOrderBookSpec extends AbstractOrderBookSpec {

  import markets.RandomOrderGenerator._

  val prng = new Random(33)

  def askOrderBookFactory(tradable: Tradable) = SortedOrderBook[AskOrder](tradable)

  def bidOrderBookFactory(tradable: Tradable) = SortedOrderBook[BidOrder](tradable)

  feature(s"A SortedOrderBook should be able to remove the first AskOrder.") {

    scenario(s"Removing the first AskOrder from a SortedOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.existingOrders.headOption should be(None)
      orderBook.sortedOrders.headOption should be(None)
    }

    scenario(s"Removing the first AskOrder from an empty SortedOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.existingOrders.headOption should be(None)
      orderBook.sortedOrders.headOption should be(None)
    }

  }

  feature(s"A SortedOrderBook should be able to remove the first BidOrder.") {

    scenario(s"Removing the first BidOrder from a SortedOrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.existingOrders.headOption should be(None)
      orderBook.sortedOrders.headOption should be(None)
    }

    scenario(s"Removing the first BidOrder from an empty SortedOrderBook.") {
      val orderBook = bidOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.existingOrders.headOption should be(None)
      orderBook.sortedOrders.headOption should be(None)
    }

  }

}
