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
package org.economicsl.agora.markets.auctions.mutable.orderbooks.parallel


import org.economicsl.agora.markets.tradables.Tradable
import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder, MarketBidOrder}


class OrderBookSpec extends org.economicsl.agora.markets.auctions.orderbooks.OrderBookSpec[BidOrder, OrderBook[BidOrder]] {

  def orderBookFactory(tradable: Tradable): OrderBook[BidOrder] = OrderBook[BidOrder](tradable)

  feature("A mutable.OrderBook should be able to be built from specified type parameters.") {

    scenario("Creating a mutable.OrderBook using generic constructor.") {
      val orderBook = OrderBook[AskOrder](validTradable)
      assert(orderBook.isInstanceOf[OrderBook[AskOrder]])
    }
  }

  feature(s"A mutable.OrderBook should be able to add a BidOrder.") {

    val orderBook = orderBookFactory(validTradable)

    scenario(s"Adding a valid bid order to an mutable.OrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
    }

    scenario(s"Adding an invalid bid order to an mutable.OrderBook.") {
      val invalidOrder = orderGenerator.nextLimitBidOrder(None, invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A mutable.OrderBook should be able to find a BidOrder.") {

    scenario(s"Finding an existing LimitBidOrder in an mutable.OrderBook.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[LimitBidOrder])
      foundOrder should be(Some(limitOrder))
    }

    scenario(s"Finding a MarketBidOrder in an mutable.OrderBook containing only LimitBidOrder instances.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[MarketBidOrder])
      foundOrder should be(None)
    }

  }

  feature(s"A mutable.OrderBook should be able to remove the head BidOrder.") {

    scenario(s"Removing the head BidOrder from an mutable.OrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing the head BidOrder from an empty mutable.OrderBook.") {
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A mutable.OrderBook should be able to remove a BidOrder.") {

    scenario(s"Removing an existing bid order from an mutable.OrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing a bid order from an empty mutable.OrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A mutable.OrderBook should be able to filter its existingOrders.") {

    scenario(s"Finding all existing MarketBidOrder instances an mutable.OrderBook.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketBidOrder])
      filteredOrders should be(Some(Iterable(marketOrder)))
    }

    scenario(s"Finding all MarketBidOrder in an mutable.OrderBook containing only LimitBidOrder instances.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketBidOrder])
      filteredOrders should be(None)
    }

  }

}
