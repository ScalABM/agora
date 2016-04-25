package markets.orders.orderings.ask

import markets.orders.AskOrder
import markets.orders.orderings.PriceOrdering


/** Mixin trait defined price priority rule for [[markets.orders.AskOrder `AskOrder`]].
  * @note This trait must be mixed in with the [[PriceOrdering `PriceOrdering[AskOrder]`]] trait.
  */
trait AskPricePriority extends {
  this: PriceOrdering[AskOrder] =>

  def hasPricePriority(order1: AskOrder, order2: AskOrder): Boolean = {
    order1.price < order2.price
  }

}
