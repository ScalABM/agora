package markets.engines.orderbooks

import markets.MarketsTestKit
import markets.orders.Order
import markets.tradables.Tradable
import org.scalatest.{FeatureSpec, Matchers}


abstract class SortedOrderBookSpec[A <: Order](name: String) extends FeatureSpec
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

  def orderBookFactory(tradable: Tradable): PriorityOrderBook[A]

  feature(s"A $name should be able to add orders.") {

    val orderBook = orderBookFactory(validTradable)

    scenario(s"Adding a valid order to a $name.") {
      val order = generateRandomOrder(tradable=validTradable)
      val result = orderBook.add(order)
      assert(result.isSuccess)
      orderBook.existingOrders.headOption should be(Some((order.uuid, order)))
      orderBook.prioritisedOrders.headOption should be(Some(order))
    }

    scenario(s"Adding an invalid order to an $name.") {
      val invalidOrder = generateRandomOrder(tradable=invalidTradable)
      val result = orderBook.add(invalidOrder)
      assert(result.isFailure)
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
      orderBook.prioritisedOrders.headOption should be(None)
    }

    scenario(s"Removing an order from an empty $name.") {
      val order = generateRandomOrder(tradable=validTradable)
      val orderBook = orderBookFactory(validTradable)
      val removedOrder = orderBook.remove(order.uuid)  // note that order has not been added!
      removedOrder should be(None)
    }

  }

}
