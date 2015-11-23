package markets.orders.orderings.bid

import markets.orders.BidOrder


trait BidPricePriority extends {

  def hasPricePriority(order1: BidOrder, order2: BidOrder): Boolean = {
    order1.price > order2.price
  }

}
