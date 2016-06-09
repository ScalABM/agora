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


/** Trait representing an order to sell a Tradable object. */
trait AskOrder extends Order {

  /** Determines whether the `AskOrder` crosses a given `BidOrder`.
    *
    * @return true if the `AskOrder` crosses a given `BidOrder`; false otherwise.
    * @note This partial function is only defined for bid orders for the same `Tradable` as the
    *       `AskOrder` and will generate a `MatchError` if called with a bid order for any other
    *       `Tradable`.
    */
  def crosses: PartialFunction[BidOrder, Boolean] = {
    case order: BidOrder if this.tradable == order.tradable => this.price <= order.price
  }

  /** Splits an existing `AskOrder` into two separate orders.
    *
    * @param residualQuantity the quantity of the residual, unfilled portion of the `AskOrder`.
    * @return a tuple of ask orders.
    * @note The first order in the tuple represents the filled portion of the `AskOrder`; the
    *       second order in the tuple represents the residual, unfilled portion of the `AskOrder`.
    */
  def split(residualQuantity: Long): (AskOrder, AskOrder)

}
