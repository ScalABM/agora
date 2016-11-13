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
package org.economicsl.agora.markets.tradables


/** Mixin trait defining a price for a `Tradable`. */
trait LimitPrice {
  this: Tradable =>

  /** Numeric value representing the price for a particular `Tradable`. */
  def limit: Price

  require(limit > Price(0), "Price must be strictly positive")

}


/** Companion object for the `LimitPrice` trait.
  *
  * Defines a basic ordering for all `Tradable` objects that mixin the `LimitPrice` trait.
  */
object LimitPrice {

  /** By default, all `Tradable` instances that mixin `LimitPrice` are ordered by `price` from lowest to highest.
    *
    * @tparam T the sub-type of `Tradable with LimitPrice` that is being ordered.
    * @return and `Ordering` defined over `Tradable with LimitPrice` instances of type `T`.
    * @note if two `Tradable with LimitPrice` instances have the same `price`, then the ordering is based by `uuid`.
    */
  def ordering[T <: Tradable with LimitPrice]: Ordering[T] = Ordering.by( tradable => (tradable.limit, tradable.uuid) )

}