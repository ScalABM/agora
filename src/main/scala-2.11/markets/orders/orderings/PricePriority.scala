/*
Copyright 2016 David R. Pugh

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
package markets.orders.orderings

import markets.orders.Order


/** Mixin Trait defining a price priority between `Orders`.
  *
  * @tparam T the type of `Orders` being compared.
  * @note the `PricePriority` trait should be mixed in with an `Ordering[T]`.
  */
trait PricePriority[T <: Order] {
  this: Ordering[T] =>

  /** Determines whether an `Order` has priority over another `Order`.
    *
    * @param order1 an `Order`.
    * @param order2 some other `Order`.
    * @return `true` if `order1` has priority over `order2`; `false` otherwise.
    */
  def hasPricePriority(order1: T, order2: T): Boolean

}
