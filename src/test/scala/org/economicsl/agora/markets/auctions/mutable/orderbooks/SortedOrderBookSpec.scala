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

import java.util.UUID

import org.economicsl.agora.markets.auctions.mutable.orderbooks.SortedOrderBook
import org.economicsl.agora.markets.auctions.orderbooks
import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder, MarketBidOrder}
import org.economicsl.agora.markets.tradables.Tradable

import scala.collection.mutable


class SortedOrderBookSpec extends orderbooks.OrderBookSpec[BidOrder, SortedOrderBook[BidOrder, mutable.Map[UUID, BidOrder]]] {

  def orderBookFactory(tradable: Tradable): SortedOrderBook[BidOrder, mutable.Map[UUID, BidOrder]] = SortedOrderBook[BidOrder](tradable)

  feature(s"A mutable.SortedOrderBook should be able to find a BidOrder.") {

    scenario(s"Finding an existing LimitBidOrder in an mutable.SortedOrderBook.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[LimitBidOrder])
      foundOrder should be(Some(limitOrder))
    }

    scenario(s"Finding a MarketBidOrder in an mutable.SortedOrderBook containing only LimitBidOrder instances.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val anotherLimitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[MarketBidOrder])
      foundOrder should be(None)
    }

  }


  feature(s"A mutable.SortedOrderBook should be able to add bid orders.") {

    val orderBook = orderBookFactory(validTradable)

    scenario(s"Adding a valid bid order to a mutable.SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
      orderBook.existingOrders.headOption should be(Some((order.uuid, order)))
    }

    scenario(s"Adding an invalid bid order to a mutable.SortedOrderBook.") {
      val invalidOrder = orderGenerator.nextLimitBidOrder(None, invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A mutable.SortedOrderBook should be able to remove the head BidOrder.") {

    scenario(s"Removing the head BidOrder from a mutable.SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing the head BidOrder from an empty mutable.SortedOrderBook.") {
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable.SortedOrderBook should be able to remove bid orders.") {

    scenario(s"Removing an existing bid order from a mutable.SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing a bid order from an empty mutable.SortedOrderBook.") {
      val order = orderGenerator.nextLimitBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable.SortedOrderBook should be able to filter its existingOrders.") {

    scenario(s"Finding all existing MarketBidOrder instances an mutable.SortedOrderBook.") {
      val limitOrder = orderGenerator.nextLimitBidOrder(None, validTradable)
      val marketOrder = orderGenerator.nextMarketBidOrder(None, validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketBidOrder])
      filteredOrders should be(Some(Iterable(marketOrder)))
    }

    scenario(s"Finding all MarketBidOrder in an mutable.SortedOrderBook containing only LimitBidOrder instances.") {
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
