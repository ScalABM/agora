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

import markets.MarketsTestKit
import markets.orders.Order
import markets.tradables.Tradable
import org.scalatest.{FeatureSpec, Matchers}


abstract class OrderBookSpec[A <: Order](name: String) extends FeatureSpec
  with Matchers
  with MarketsTestKit {

  /** Generate a random `Order`.
    *
    * @param marketOrderProbability probability of generating a `MarketOrder`.
    * @param minimumPrice lower bound on the price for a `LimitOrder`.
    * @param maximumPrice upper bound on the price for a `LimitOrder`.
    * @param minimumQuantity lower bound on the `Order` quantity.
    * @param maximumQuantity upper bound on the `Order` quantity.
    * @param timestamp a timestamp for the `Order`.
    * @param tradable the `Order` validTradable.
    * @return either `LimitOrder` or `MarketOrder`, depending.
    */
  def generateRandomOrder(marketOrderProbability: Double = 0.5,
                          minimumPrice: Long = 1,
                          maximumPrice: Long = Long.MaxValue,
                          minimumQuantity: Long = 1,
                          maximumQuantity: Long = Long.MaxValue,
                          timestamp: Long = 1,
                          tradable: Tradable): A

  def orderBookFactory(tradable: Tradable): OrderBook[A]

  feature(s"A $name should be able to add orders.") {

    val orderBook = orderBookFactory(validTradable)

    scenario(s"Adding a valid order to a $name.") {
      val order = generateRandomOrder(tradable=validTradable)
      val result = orderBook.add(order)
      orderBook.existingOrders.headOption should be(Some((order.uuid, order)))
    }

    scenario(s"Adding an invalid order to an $name.") {
      val invalidOrder = generateRandomOrder(tradable=invalidTradable)
      intercept[IllegalArgumentException] {
        orderBook.add(invalidOrder)
      }
    }

  }

  feature(s"A $name should be able to remove orders.") {

    scenario(s"Removing an existing order from a $name.") {
      val order = generateRandomOrder(tradable=validTradable)
      val orderBook = orderBookFactory(validTradable)
      orderBook.add(order)
      val removedOrder = orderBook.remove(order.uuid)
      removedOrder should be(Some(order))
      orderBook.existingOrders.headOption should be(None)
    }

    scenario(s"Removing an order from an empty $name.") {
      val order = generateRandomOrder(tradable=validTradable)
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
      orderBook.existingOrders.headOption should be(None)
    }

  }
  
}
