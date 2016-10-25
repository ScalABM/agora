package markets.orderbooks.mutable

import java.util.UUID

import markets.tradables.Tradable
import markets.tradables.orders.ask.AskOrder

import scala.collection.mutable


/** This class implements a `mutable.AskOrderBook` using a `mutable.LinkedHashMap` to store `AskOrder` instances.
  *
  * @param tradable all `AskOrder` instances stored in the `mutable.LinkedHashAskOrderBook` should be for the same `Tradable`.
  * @tparam A the type of `AskOrder` stored in the `mutable.LinkedHashAskOrderBook`.
  * @note the underlying collection `existingOrders` is implemented as a `mutable.LinkedHasMap` which implements a
  *       `mutable.Map` using a hash table. All traversal methods of this class will visit the `AskOrder` instances in
  *       the order in which they were inserted.
  */
class LinkedHashAskOrderBook[A <: AskOrder](val tradable: Tradable)
  extends AskOrderBook[A, mutable.LinkedHashMap[UUID, A]] {

  /* Underlying collection of `Order` instances. */
  protected val existingOrders = mutable.LinkedHashMap.empty[UUID, A]

}


object LinkedHashAskOrderBook {

  def apply[A <: AskOrder](tradable: Tradable): LinkedHashAskOrderBook[A] = {
    new LinkedHashAskOrderBook[A](tradable)
  }

  def apply[A <: AskOrder](initialOrders: Iterable[A], tradable: Tradable): LinkedHashAskOrderBook[A] = {
    val askOrderBook = LinkedHashAskOrderBook[A](tradable)
    initialOrders.foreach(order => askOrderBook.add(order))
    askOrderBook
  }

}