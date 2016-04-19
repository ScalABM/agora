/*
Copyright 2016 David R. Pugh

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


/** Trait representing an Bid order.
  *
  * A Bid order is an order to buy a security. The BidOrder trait should be mixed in with
  * each specific type of order (i.e., limit orders, market orders, etc).
  *
  */
trait BidOrder extends Order {

  /** Determines whether the `BidOrder` crosses a particular `AskOrder`.
    *
    * @return true if the `AskOrder` crosses the `BidOrder`; false otherwise.
    * @note This partial function is only defined for ask orders for the same `Tradable` as the
    *       `BidOrder` and will generate a `MatchError` if called with an ask order for any other
    *       `Tradable`.
    */
  def crosses: PartialFunction[AskOrder, Boolean] = {
    case order: AskOrder if this.tradable == order.tradable => this.price >= order.price
  }

  /** Splits an existing `BidOrder` into two separate orders.
    *
    * @param residualQuantity
    * @return a tuple of bid orders.
    */
  def split(residualQuantity: Long): (BidOrder, BidOrder)

}

