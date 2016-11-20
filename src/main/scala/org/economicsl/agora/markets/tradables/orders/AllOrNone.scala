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
package org.economicsl.agora.markets.tradables.orders

import org.economicsl.agora.markets.tradables.{Quantity, Tradable}


/** Mixin trait indicating that an order must be executed in its entirety or not executed at all. */
trait AllOrNone[-T <: Tradable with Quantity] extends NonPriceCriteria[T] {
  this: Order with PriceCriteria[T] with Quantity =>

  /** Additional, non-price criteria used to determine whether some `Tradable` is acceptable.
    *
    * @note Partial execution is not acceptable; an `AllOfNone` order will only execute if there is a sufficient
    *       quantity of the `Tradable` available in a single transaction to cover it.
    */
  val nonPriceCriteria: Option[(T) => Boolean] = Some(order => this.quantity >= order.quantity)

}
