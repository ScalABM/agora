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
package markets.tradables


/** Mixin trait defining a price for a `Tradable`. */
trait LimitPrice {
  this: Tradable =>

  /** Numeric value representing the price for a particular `Tradable`. */
  def limit: Long

  require(limit > 0, "Price must be non-negative")

}


/** Companion object for the `LimitPrice` trait.
  *
  * The companion object provides a default ordering for all `Tradable` objects that mixin the `LimitPrice` trait.
  */
object LimitPrice {

  /** By default, all `Tradable` instances that mixin `LimitPrice` are ordered by `price` from lowest to highest.
    *
    * @tparam T the subtype of `Tradable with LimitPrice` that is being ordered.
    * @return and `Ordering` defined over `Tradable` instances of type `T`.
    * @note if `Tradable with LimitPrice` have the same `price`, then these instances are ordered by `uuid`.
    */
  implicit def ordering[T <: Tradable with LimitPrice]: Ordering[T] = {
    Ordering.by( tradable => (tradable.limit, tradable.uuid) )
  }

}