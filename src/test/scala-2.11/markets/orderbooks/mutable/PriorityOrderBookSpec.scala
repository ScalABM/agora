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
package markets.orderbooks.mutable

import markets.orderbooks.AbstractOrderBookSpec
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable

import scala.util.Random


class PriorityOrderBookSpec extends AbstractOrderBookSpec {

  import markets.RandomOrderGenerator._

  val prng = new Random(1234)

  def askOrderBookFactory(tradable: Tradable) = PriorityOrderBook[AskOrder](tradable)

  def bidOrderBookFactory(tradable: Tradable) = PriorityOrderBook[BidOrder](tradable)

  feature(s"A mutable.PriorityOrderBook should be able to add ask orders.") {

    val orderBook = askOrderBookFactory(validTradable)

    scenario(s"Adding a valid ask order to a mutable.PriorityOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
      orderBook.existingOrders.headOption should be(Some((order.uuid, order)))
    }

    scenario(s"Adding an invalid ask order to a mutable.PriorityOrderBook.") {
      val invalidOrder = randomAskOrder(prng, tradable=invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A mutable.PriorityOrderBook should be able to remove ask orders.") {

    scenario(s"Removing an existing ask order from a mutable.PriorityOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing an ask order from an empty mutable.PriorityOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable.PriorityOrderBook should be able to add bid orders.") {

    val orderBook = bidOrderBookFactory(validTradable)

    scenario(s"Adding a valid bid order to a mutable.PriorityOrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      orderBook.add(order)
      orderBook.headOption should be(Some(order))
      orderBook.existingOrders.headOption should be(Some((order.uuid, order)))
    }

    scenario(s"Adding an invalid bid order to a mutable.PriorityOrderBook.") {
      val invalidOrder = randomBidOrder(prng, tradable=invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A mutable.PriorityOrderBook should be able to remove the head AskOrder.") {

    scenario(s"Removing the head AskOrder from a mutable.PriorityOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing the head AskOrder from an empty mutable.PriorityOrderBook.") {
      val order = randomAskOrder(prng, tradable=validTradable)
      val orderBook = askOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable.PriorityOrderBook should be able to remove the head BidOrder.") {

    scenario(s"Removing the head BidOrder from a mutable.PriorityOrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove()
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing the head BidOrder from an empty mutable.PriorityOrderBook.") {
      val orderBook = bidOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove()  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

  feature(s"A mutable.PriorityOrderBook should be able to remove bid orders.") {

    scenario(s"Removing an existing bid order from a mutable.PriorityOrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing a bid order from an empty mutable.PriorityOrderBook.") {
      val order = randomBidOrder(prng, tradable=validTradable)
      val orderBook = bidOrderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.headOption should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }

}

