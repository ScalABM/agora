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
package markets.parallel.concurrent.orderbooks

import markets.generic
import markets.generic.AbstractOrderBookSpec
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable

import scala.util.Random


class OrderBookSpec extends AbstractOrderBookSpec {

  import markets.RandomOrderGenerator._

  val prng = new Random(1776)

  def askOrderBookFactory(tradable: Tradable): generic.OrderBook[AskOrder] = OrderBook[AskOrder](tradable)

  def bidOrderBookFactory(tradable: Tradable): generic.OrderBook[BidOrder] = OrderBook[BidOrder](tradable)

  feature(s"A concurrent.OrderBook should be able to add ask orders.") {

    val orderBook = askOrderBookFactory(validTradable)

    scenario(s"Adding a valid ask order to an concurrent.OrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
    }

    scenario(s"Adding an invalid ask order to an concurrent.OrderBook.") {
      val invalidOrder = randomAskOrder(prng, tradable=invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A concurrent.OrderBook should be able to remove ask orders.") {

    scenario(s"Removing an existing ask order from an concurrent.OrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing an ask order from an empty concurrent.OrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to add bid orders.") {

    val orderBook = bidOrderBookFactory(validTradable)

    scenario(s"Adding a valid bid order to an concurrent.OrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
    }

    scenario(s"Adding an invalid bid order to an concurrent.OrderBook.") {
      val invalidOrder = randomBidOrder(prng, tradable=invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A concurrent.OrderBook should be able to find an AskOrder.") {

    scenario(s"Finding an existing LimitAskOrder in an concurrent.OrderBook.") {
      val limitOrder = randomAskOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val marketOrder = randomAskOrder(prng, marketOrderProbability=1.0, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[LimitAskOrder])
      foundOrder should be(Some(limitOrder))
    }

    scenario(s"Finding a MarketAskOrder in an concurrent.OrderBook containing only LimitAskOrder instances.") {
      val limitOrder = randomAskOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val anotherLimitOrder = randomAskOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[MarketAskOrder])
      foundOrder should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to find a BidOrder.") {

    scenario(s"Finding an existing LimitBidOrder in an concurrent.OrderBook.") {
      val limitOrder = randomBidOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val marketOrder = randomBidOrder(prng, marketOrderProbability=1.0, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[LimitBidOrder])
      foundOrder should be(Some(limitOrder))
    }

    scenario(s"Finding a MarketBidOrder in an concurrent.OrderBook containing only LimitBidOrder instances.") {
      val limitOrder = randomBidOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val anotherLimitOrder = randomBidOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val foundOrder = orderBook.find(order => order.isInstanceOf[MarketBidOrder])
      foundOrder should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to remove the head AskOrder.") {

    scenario(s"Removing the head AskOrder from an concurrent.OrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing the head AskOrder from an empty concurrent.OrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to remove the head BidOrder.") {

    scenario(s"Removing the head BidOrder from an concurrent.OrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing the head BidOrder from an empty concurrent.OrderBook.") {
      val orderBook = bidOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to remove bid orders.") {

    scenario(s"Removing an existing bid order from an concurrent.OrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
    }

    scenario(s"Removing a bid order from an empty concurrent.OrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
    }

  }

  feature(s"A concurrent.OrderBook should be able to filter its existingOrders.") {

    scenario(s"Finding all existing MarketBidOrder instances an concurrent.OrderBook.") {
      val limitOrder = randomBidOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val marketOrder = randomBidOrder(prng, marketOrderProbability=1.0, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketBidOrder])
      filteredOrders should be(Some(Iterable(marketOrder)))
    }

    scenario(s"Finding all MarketBidOrder in an concurrent.OrderBook containing only LimitBidOrder instances.") {
      val limitOrder = randomBidOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val anotherLimitOrder = randomBidOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketBidOrder])
      filteredOrders should be(None)
    }

    scenario(s"Finding all existing MarketAskOrder instances an concurrent.OrderBook.") {
      val limitOrder = randomAskOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val marketOrder = randomAskOrder(prng, marketOrderProbability=1.0, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(marketOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketAskOrder])
      filteredOrders should be(Some(Iterable(marketOrder)))
    }

    scenario(s"Finding all MarketAskOrder in an concurrent.OrderBook containing only LimitAskOrder instances.") {
      val limitOrder = randomAskOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val anotherLimitOrder = randomAskOrder(prng, marketOrderProbability=0.0, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(limitOrder)
      orderBook.add(anotherLimitOrder)
      val filteredOrders = orderBook.filter(order => order.isInstanceOf[MarketAskOrder])
      filteredOrders should be(None)
    }

  }
  
}
