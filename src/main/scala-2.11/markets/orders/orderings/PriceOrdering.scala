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
package markets.orders.orderings

import markets.orders.Order


/** Trait defining price ordering over various subclasses of [[markets.orders.Order `Order`]]. */
trait PriceOrdering[T <: Order] extends Ordering[T] {

  def compare(order1: T, order2: T): Int = {
    if (hasPricePriority(order1, order2)) {
      -1
    } else if (order1.price == order2.price) {
      0
    } else {
      1
    }
  }

  /** Should return true if `order1` has price priority over `order2`. */
  def hasPricePriority(order1: T, order2: T): Boolean

}
