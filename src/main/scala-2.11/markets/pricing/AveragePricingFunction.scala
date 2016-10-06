package markets.pricing

import java.util.UUID

import markets.orderbooks.OrderBook
import markets.tradables.orders.Order
import markets.tradables.Price

import scala.collection.GenMap


class AveragePricingFunction[O1 <: Order with Price, O2 <: Order with Price](val gamma: Double) extends PricingFunction[O1, O2] {

  require(0 <= gamma && gamma <= 1, "Price must be individually rational!")

  def apply(order1: O1, order2: O2): Long = gamma * order1.price + (1 - gamma) * order2.price  // could lead to overflow!

  def apply(orderBook1: OrderBook[O1, GenMap[UUID, O1]], orderBook2: OrderBook[O2, GenMap[UUID, O2]]): Long = {
    // find the break-even ask and bid orders
    // gamma * breakEvenAskOrder.price + (1 - gamma) * breakEvenBidOrder.price
    ???
  }

}


object AveragePricingFunction {

  def apply[O1 <: Order with Price, O2 <: Order with Price](gamma: Double): AveragePricingFunction[O1, O2] = {
    new AveragePricingFunction[O1, O2](gamma)
  }

}