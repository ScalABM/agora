package markets.orderbooks

import java.util.UUID

import markets.orders.Order
import markets.tradables.Tradable

import scala.collection.generic


/** Trait defining the interface for an `OrderBook`.
  *
  * @tparam O type of `Order` stored in the order book.
  */
trait OrderBook[O <: Order, CC <: collection.GenMap[UUID, O]] {

  /** All `Orders` contained in an `OrderBook` should be for the same `Tradable`. */
  def tradable: Tradable

  /** Add an `Order` to the `OrderBook`.
    *
    * @param order the `Order` that should be added to the `OrderBook`.
    */
  def add(order: O): Unit

  /** Filter the `OrderBook` and return those `Order` instances satisfying the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return collection of `Order` instances satisfying the given predicate.
    */
  def filter(p: (O) => Boolean): Option[collection.GenIterable[O]] = {
    val filteredOrders = existingOrders.filter { case (_, order) => p(order) }
    if (filteredOrders.nonEmpty) Some(filteredOrders.values) else None
  }

  /** Find the first `Order` in the `OrderBook` that satisfies the given predicate.
    *
    * @param p predicate defining desirable `Order` characteristics.
    * @return `None` if no `Order` in the `OrderBook` satisfies the predicate; `Some(order)` otherwise.
    */
  def find(p: (O) => Boolean): Option[O] = existingOrders.find { case (_, order) => p(order) } match {
    case Some((_, order)) => Some(order)
    case None => None
  }

  /** Return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def headOption: Option[O] = existingOrders.values.headOption

  /** Remove and return the head `Order` of the `OrderBook`.
    *
    * @return `None` if the `OrderBook` is empty; `Some(order)` otherwise.
    */
  def remove(): Option[O] = {
    headOption match {
      case Some(order) => remove(order.uuid)
      case None => None
    }
  }

  /** Remove and return an existing `Order` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the `OrderBook`; `Some(order)` otherwise.
    */
  def remove(uuid: UUID): Option[O]

  /* Underlying collection of `Order` instances. */
  protected def existingOrders: CC

}


object OrderBook {

  import scala.collection.mutable
  import scala.collection.parallel


  def apply[O <: Order, CC <: mutable.Map[UUID, O]](tradable: Tradable): OrderBook[O, CC] = {
    new MutableOrderBook[O, CC](tradable)
  }

  def apply[O <: Order, CC <: parallel.mutable.ParMap[UUID, O]](tradable: Tradable): OrderBook[O, CC] = {
    ??? //new ParallelMutableOrderBook[O, CC](tradable)
  }

  private class MutableOrderBook[O <: Order, CC <: mutable.Map[UUID, O]](val tradable: Tradable)(implicit cbf: generic.CanBuildFrom[Nothing, (UUID, O), CC])
    extends OrderBook[O, CC] {

    /** Add an `Order` to the `OrderBook`.
      *
      * @param order the `Order` that should be added to the `OrderBook`.
      */
    def add(order: O): Unit = {
      require(order.tradable == tradable)  // can be disabled by compiler!
      existingOrders(order.uuid) = order
    }

    /** Remove and return an existing `Order` from the `OrderBook`.
      *
      * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
      * @return `None` if the `uuid` is not found in the `OrderBook`; `Some(order)` otherwise.
      */
    def remove(uuid: UUID): Option[O] = existingOrders.remove(uuid)

    /* Underlying collection of `Order` instances. */
    protected val existingOrders: CC = cbf().result()

  }

}
