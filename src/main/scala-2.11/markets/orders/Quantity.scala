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


/** Mixin trait for `Tradable` that requires a quantity. */
trait Quantity {
  this: Tradable =>

  /** The quantity of the `Tradable`. */
  def quantity: Long

  require(quantity > 0, "Quantity must be strictly positive.")

}
