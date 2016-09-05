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


/** Trait representing an order to buy a `Tradable` object. */
trait BidOrder extends Order {

  /** A boolean function that defines the set of acceptable `AskOrder` instances.
    *
    * @return a boolean function.
    */
  def predicate: AskOrder => Boolean

  /** Splits an existing `BidOrder` into two separate orders.
    *
    * @param residualQuantity the quantity of the residual, unfilled portion of the `BidOrder`.
    * @return a tuple of bid orders.
    * @note The first order in the tuple represents the filled portion of the `BidOrder`; the
    *       second order in the tuple represents the residual, unfilled portion of the `BidOrder`.
    */
  def split(residualQuantity: Long): (BidOrder, BidOrder)

}


object BidOrder {

  /** By default, the highest priority `BidOrder` is the one with the highest `price`. */
  implicit def pricePriority[A <: BidOrder]: Ordering[A] = Order.priceOrdering

}