package markets.engines.orderbooks.mutable

import markets.orders.Order

import scala.collection.mutable


trait MutableTreeSetLike[A <: Order] {

  def ordering: Ordering[A]

  /** Add an order to the order book.
    *
    * @param order the order that is to be added to the order book.
    */
  def add(order: A): Unit = {
    backingStore += order
  }

  /** Remove an order from the order book.
    *
    * @param order the order that is to be removed from the order book.
    */
  def remove(order: A): Unit = {
    backingStore -= order
  }

  protected val backingStore: mutable.TreeSet[A]

}
