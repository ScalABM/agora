package markets.engines.orderbooks.mutable

import java.util.UUID

import markets.engines.orderbooks.Sorted
import markets.orders.AskOrder
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Try


class SortedAskOrderBook(val ordering: Ordering[AskOrder], tradable: Tradable)
  extends AskOrderBook(tradable) with Sorted[AskOrder] {

  /** Add an `AskOrder` to the `OrderBook`.
    *
    * @param order the `AskOrder` that should be added to the `AskOrderBook`.
    * @return `Success()` if the `order` is added to the `AskOrderBook`; `Failure(ex)` otherwise.
    * @note Underlying implementation of `existingOrders` uses a `mutable.HashMap` in order to
    *       guarantee that adding an `AskOrder` to the `AskOrderBook` is an `O(1)` operation.
    */
  override def add(order: AskOrder): Try[Unit] = super.add(order) match {
    // add to sortedExistingOrders!
  }

  /** Remove and return an existing `AskOrder` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note Underlying implementation of `existingOrders` uses a `mutable.HashMap` in order to
    *       guarantee that removing an `AskOrder` from the `AskOrderBook` is an `O(1)` operation.
    */
  override def remove(uuid: UUID): Option[AskOrder] = super.remove(uuid) match {
    // remove from sortedExistingOrders!
  }

  /* Protected at the package level to simplify testing. */
  protected[orderbooks] val sortedExistingOrders = mutable.TreeSet.empty[AskOrder]

}
