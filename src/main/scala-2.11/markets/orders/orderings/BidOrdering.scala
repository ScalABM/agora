package markets.orders.orderings

import markets.orders.BidOrderLike


trait BidOrdering extends Ordering[BidOrderLike] {

  def hasPricePriority(order1: BidOrderLike, order2: BidOrderLike): Boolean = {
    order1.price > order2.price
  }

}
