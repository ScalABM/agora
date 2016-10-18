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

import markets.tradables.{LimitPrice, Tradable}


/** Mixin trait defining additional, non-price criteria used to determine whether some `Tradable` is acceptable. */
sealed trait AdditionalCriteria[-T <: Tradable] {
  this: Tradable with LimitPrice with Predicate[T] =>

  /** Additional, non-price criteria used to determine whether some `Tradable` is acceptable. */
  def additionalCriteria: Option[(T) => Boolean]

}


/** Mixin trait used when there are no additional, non-price criteria needed to determine whether some `Tradable` is acceptable. */
trait NoAdditionalCriteria[-T <: Tradable] extends AdditionalCriteria[T] {
  this: Tradable with LimitPrice with Predicate[T] =>

  /** Additional, non-price criteria used to determine whether some `Tradable` is acceptable. */
  def additionalCriteria: Option[(T) => Boolean] = None

}