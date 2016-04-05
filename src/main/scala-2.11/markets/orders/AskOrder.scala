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


/** Trait representing an Ask order.
  *
  * An Ask order is an order to sell a Tradable object. The AskOrder trait should be mixed in
  * with each specific type of order (i.e., limit orders, market orders, etc).
  *
  */
trait AskOrder extends Order {

  /** Determines whether the AskOrder crosses a particular BidOrder.
    *
    * @param order
    * @return
    */
  def crosses(order: BidOrder): Boolean

  /** Splits an existing `AskOrder` into two separate orders.
    *
    * @param residualQuantity
    * @return a tuple of ask orders.
    */
  def split(residualQuantity: Long): (AskOrder, AskOrder)

}
