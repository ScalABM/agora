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
package markets.orderbooks

import java.util.UUID

import markets.tradables.orders.Order


/** Abstract class defining the interface for a `SortedOrderBook`.
  *
  * @tparam O the type of `Order` stored in a `SortedOrderBook`.
  * @tparam CC1 type of underlying collection class used to store the `Order` instances.
  * @tparam CC2 type of underlying collection class used to store the sorted `Order` instances.
  */
trait SortedOrders[O <: Order, +CC1 <: collection.GenMap[UUID, O], +CC2 <: collection.SortedSet[O]] {
  this: OrderBook[O, CC1] =>

  /* Underlying sorted collection of `Order` instances. */
  protected def sortedOrders: CC2

}

