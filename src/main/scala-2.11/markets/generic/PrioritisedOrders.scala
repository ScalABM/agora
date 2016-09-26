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
package markets.generic

import java.util.UUID

import markets.orders.Order

import scala.collection.mutable


/** Abstract class defining the interface for a `PriorityOrderBook`.
  *
  * @tparam O the type of `Order` stored in a `PriorityOrderBook`.
  * @tparam CC type of underlying collection class used to store the `Order` instances.
  */
trait PrioritisedOrders[O <: Order, CC <: mutable.Map[UUID, O]] {
  this: OrderBook[O, CC] =>

  /* Underlying prioritised collection of `Order` instances. */
  protected def prioritisedOrders: mutable.PriorityQueue[O]

}

