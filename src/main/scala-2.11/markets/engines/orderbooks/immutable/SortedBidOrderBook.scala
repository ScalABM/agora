/*
Copyright 2016 ScalABM

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package markets.engines.orderbooks.immutable

import java.util.UUID

import markets.engines.orderbooks.Sorted
import markets.orders.BidOrder
import markets.tradables.Tradable

import scala.collection.immutable
import scala.util.{Failure, Success, Try}


/** Class representing a sorted `BidOrderBook`.
  *
  * @param tradable All `BidOrder` instances contained in the `BidOrderBook` should be for the
  *                 same `Tradable`.
  * @param ordering An ordering over `BidOrder` instances.
  */
class SortedBidOrderBook(tradable: Tradable)(implicit val ordering: Ordering[BidOrder])
  extends BidOrderBook(tradable) with Sorted[BidOrder] {

  /** Add an `BidOrder` to the `OrderBook`.
    *
    * @param order the `BidOrder` that should be added to the `BidOrderBook`.
    * @return `Success()` if the `order` is added to the `BidOrderBook`; `Failure(ex)` otherwise.
    * @note Underlying implementation of `existingSortedOrders` uses a `mutable.TreeMap` in order to
    *       guarantee that adding an `BidOrder` to the `BidOrderBook` is an `O(log n)` operation.
    */
  override def add(order: BidOrder): Try[Unit] = super.add(order) match {
    case Success(_) => Try(sortedExistingOrders += order)
    case failure @ Failure(ex) => failure
  }

  /** Remove and return an existing `BidOrder` from the `OrderBook`.
    *
    * @param uuid the `UUID` for the order that should be removed from the `OrderBook`.
    * @return `None` if the `uuid` is not found in the order book; `Some(order)` otherwise.
    * @note Underlying implementation of `existingSortedOrders` uses a `mutable.TreeMap` in order to
    *       guarantee that removing an `BidOrder` from the `BidOrderBook` is an `O(log n)`
    *       operation.
    */
  override def remove(uuid: UUID): Option[BidOrder] = super.remove(uuid) match {
    case residualOrder @ Some(order) => sortedExistingOrders -= order; residualOrder
    case residualOrder @ None => residualOrder
  }

  /* Protected at the package level to simplify testing. */
  @volatile
  protected[orderbooks] var sortedExistingOrders = immutable.TreeSet.empty[BidOrder](ordering)

}


object SortedBidOrderBook {

  /** Auxiliary constructor for a `SortedBidOrderBook`.
    *
    * @param tradable All `BidOrder` instances contained in the `BidOrderBook` should be for the
    *                 same `Tradable`.
    * @param ordering An ordering over `BidOrder` instances.
    * @return an instance of a `SortedBidOrderBook`.
    */
  def apply(tradable: Tradable)(implicit ordering: Ordering[BidOrder]): SortedBidOrderBook = {
    new SortedBidOrderBook(tradable)(ordering)
  }

}