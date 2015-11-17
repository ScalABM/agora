package markets.orders.orderings

import markets.orders.AskOrderLike

trait AskPricePriority extends {

  def hasPricePriority(order1: AskOrderLike, order2: AskOrderLike): Boolean = {
    order1.price < order2.price
  }

}
