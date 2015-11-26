package markets.orders.orderings.ask

import markets.orders.AskOrder

trait AskPricePriority extends {

  def hasPricePriority(order1: AskOrder, order2: AskOrder): Boolean = {
    order1.price < order2.price
  }

}
