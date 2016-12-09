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
package markets.orderbooks.concurrent

import markets.orderbooks
import markets.tradables.orders.ask.{AskOrder, LimitAskOrder, MarketAskOrder}
import markets.tradables.Tradable


class OrderBookSpec extends orderbooks.OrderBookSpec[AskOrder, OrderBook[AskOrder]] {

  def orderBookFactory(tradable: Tradable): OrderBook[AskOrder] = OrderBook[AskOrder](tradable)

  feature(s"A concurrent.OrderBook should be able to add ask orders.") {

    val orderBook = orderBookFactory(validTradable)

    scenario(s"Adding a valid ask order to an concurrent.OrderBook.") {
      val order = orderGenerator.nextLimitAskOrder(None, validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
    }

    scenario(s"Adding an invalid ask order to an concurrent.OrderBook.") {
      val invalidOrder = orderGenerator.nextLimitAskOrder(None, invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A concurrent.OrderBook should be able to remove ask orders.") {

    scenario(s"Removing an existing ask order from an concurrent.OrderBook.") {
      val order = orderGenerator.nextLimitAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing an ask order from an empty concurrent.OrderBook.") {
      val order = orderGenerator.nextLimitAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to find an AskOrder.") {

    scenario(s"Finding an existing LimitAskOrder in an concurrent.OrderBook.") {
      val limitOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      val marketOrder = orderGenerator.nextMarketAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[LimitAskOrder])
      foundOrder should be(Some(limitOrder))
    }

    scenario(s"Finding a MarketAskOrder in an concurrent.OrderBook containing only LimitAskOrder instances.") {
      val limitOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[MarketAskOrder])
      foundOrder should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to remove the head AskOrder.") {

    scenario(s"Removing the head AskOrder from an concurrent.OrderBook.") {
      val order = orderGenerator.nextLimitAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing the head AskOrder from an empty concurrent.OrderBook.") {
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to filter its existingOrders.") {

    scenario(s"Finding all existing MarketAskOrder instances an concurrent.OrderBook.") {
      val limitOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      val marketOrder = orderGenerator.nextMarketAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketAskOrder])
      filteredOrders should be(Some(Iterable(marketOrder)))
    }

    scenario(s"Finding all MarketAskOrder in an concurrent.OrderBook containing only LimitAskOrder instances.") {
      val limitOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitAskOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketAskOrder])
      filteredOrders should be(None)
    }

  }
  
}
