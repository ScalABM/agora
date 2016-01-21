package markets.orders.orderings.bid

import markets.orders.BidOrder
import markets.orders.orderings.PriceOrdering


/** Mixin trait defined price priority rule for [[markets.orders.BidOrder `BidOrder`]].
  * @note This trait must be mixed in with the [[PriceOrdering `PriceOrdering[BidOrder]`]] trait.
  */
trait BidPricePriority extends {
  this: PriceOrdering[BidOrder] =>

  def hasPricePriority(order1: BidOrder, order2: BidOrder): Boolean = {
    order1.price > order2.price
  }

}
