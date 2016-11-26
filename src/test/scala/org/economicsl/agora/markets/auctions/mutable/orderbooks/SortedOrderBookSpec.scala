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
package org.economicsl.agora.markets.auctions.mutable.orderbooks

import org.economicsl.agora.markets.auctions
import org.economicsl.agora.markets.tradables.orders.bid.{LimitBidOrder, PersistentMarketBidOrder}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.{Price, Tradable}

/*
class SortedOrderBookSpec extends auctions.orderbooks.OrderBookSpec[LimitBidOrder with Persistent, SortedOrderBook[LimitBidOrder with Persistent]] {

  def orderBookFactory(tradable: Tradable): SortedOrderBook[LimitBidOrder with Persistent] = {
    SortedOrderBook[LimitBidOrder with Persistent](tradable)
  }

  feature(s"A mutableSortedOrderBook should be able to find a BidOrder.") {

    scenario(s"Finding an existing LimitBidOrder in an mutable SortedOrderBook.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val foundOrder = orderBook.find(order => order.limit < Price.MaxValue)
      foundOrder should be(Some(limitOrder))
    }

    scenario(s"Finding a MarketBidOrder in an mutable SortedOrderBook containing only LimitBidOrder instances.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[PersistentMarketBidOrder])
      foundOrder should be(None)
    }

  }

  feature(s"A mutable OrderBook should be able to clear its existing orders.") {

    val orderBook = orderBookFactory(validTradable)

    scenario("Clearing existing orders from a SortedOrderBook.") {
      val numberOrders = 10
      val orders = for { i <- 1 to numberOrders} yield orderGenerator.nextLimitBidOrder(validTradable)
      orders.foreach(order => orderBook.add(order))
      orderBook.clear()
      orderBook.isEmpty should be(true)
    }

  }

  feature(s"A mutable SortedOrderBook should be able to add bid orders.") {

    val orderBook = orderBookFactory(validTradable)

    scenario(s"Adding a valid bid order to a mutable SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
      orderBook.existingOrders.headOption should be(Some((order.uuid, order)))
    }

    scenario(s"Adding an invalid bid order to a mutable SortedOrderBook.") {
      val invalidOrder = orderGenerator.nextLimitBidOrder(invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A mutable SortedOrderBook should be able to remove the head BidOrder.") {

    scenario(s"Removing the head BidOrder from a mutable SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing the head BidOrder from an empty mutable SortedOrderBook.") {
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable SortedOrderBook should be able to remove bid orders.") {

    scenario(s"Removing an existing bid order from a mutable SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing a bid order from an empty mutable SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable SortedOrderBook should be able to filter its existingOrders.") {

    scenario(s"Finding all existing MarketBidOrder instances an mutable SortedOrderBook.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[PersistentMarketBidOrder])
      filteredOrders should be(Some(Iterable(marketOrder)))
    }

    scenario(s"Finding all MarketBidOrder in an mutable SortedOrderBook containing only LimitBidOrder instances.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[PersistentMarketBidOrder])
      filteredOrders should be(None)
    }

  }

  feature(s"A mutable SortedOrderBook should maintain sorting on price.") {

    scenario(s"MarketBidOrder should take priority over LimitBidOrder.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      orderBook.headOption should be(Some(marketOrder))
    }

    scenario(s"Higher priced LimitBidOrder should take priority over lower priced LimitBidOrder.") {
      val highPriceLimitOrder = orderGenerator.nextLimitBidOrder(validTradable)
      val lowPrice = Price(highPriceLimitOrder.limit.value - 1)
      val lowPriceLimitOrder = orderGenerator.nextLimitBidOrder(lowPrice, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(highPriceLimitOrder)
      orderBook.add(lowPriceLimitOrder)
      orderBook.headOption should be(Some(highPriceLimitOrder))
    }

  }

}
*/