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
package markets.tradables.orders

import markets.tradables.{Quantity, Tradable}


/** A mixin trait indicating that an `Order` to buy or sell a `Tradable` must be filled immediately in its entirety;
  * no partial fills are allowed.
  * @tparam O
  */
trait FillOrKill[-O <: Order with Quantity] {
  this: Order with Quantity with NonPriceCriteria[O] =>

  /** An acceptable `Order` must have a quantity greater than or equal to the quantity of this `Order`. */
  def nonPriceCriteria: Option[(O) => Boolean] = Some(order => order.quantity >= this.quantity)

}
