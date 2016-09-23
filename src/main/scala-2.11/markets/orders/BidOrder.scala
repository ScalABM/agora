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


/** Trait representing an `Order` to buy a particular `Tradable`. */
trait BidOrder extends Order with Price with Quantity


object BidOrder {

  /** By default, `BidOrder` instances are ordered based on `price` from highest to lowest. */
  implicit def ordering[O <: BidOrder]: Ordering[O] = Price.ordering.reverse

  /** The highest priority `BidOrder` is the one with the highest `price`.
    *
    * @note `priority` is an `Ordering` that is specifically designed for use when `BidOrder` instances need
    *      to be stored in a `PriorityQueue`.
    */
  def priority[O <: BidOrder]: Ordering[O] = Price.ordering

}