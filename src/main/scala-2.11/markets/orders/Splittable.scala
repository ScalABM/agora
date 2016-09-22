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
package markets.orders

import markets.tradables.Tradable


/** Mixin trait defining the interface for an `Order` that can be split. */
trait Splittable[O <: Splittable[O]] extends Quantity {
  this: Tradable =>

  /** Splits an existing `Order` into two separate `Order` instances.
    *
    * @param residualQuantity the quantity of the residual, unfilled portion of the `Order`.
    * @return a tuple of bid orders.
    * @note The first order in the tuple represents the filled portion of the `Order`; the
    *       second order in the tuple represents the residual, unfilled portion of the `Order`.
    */
  def split(residualQuantity: Long): (O, O)

}
