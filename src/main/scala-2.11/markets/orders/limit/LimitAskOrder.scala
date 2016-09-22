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
package markets.orders.limit

import java.util.UUID

import markets.orders.AskOrder
import markets.tradables.{Security, Tradable}


/** Class representing an order to sell some Tradable at some price.
  *
  * @param issuer
  * @param price
  * @param quantity
  * @param timestamp
  * @param tradable
  * @param uuid
  */
case class LimitAskOrder(issuer: UUID,
                         price: Long,
                         quantity: Long,
                         timestamp: Long,
                         tradable: Tradable,
                         uuid: UUID) extends LimitOrder with AskOrder {

  /** Splits an existing `LimitAskOrder` into two separate orders.
    *
    * @param residualQuantity the quantity of the residual, unfilled portion of the `LimitAskOrder`.
    * @return a tuple of `LimitAskOrders`.
    * @note The first order in the tuple represents the filled portion of the `LimitAskOrder`; the
    *       second order in the tuple represents the residual, unfilled portion of the
    *       `LimitAskOrder`.
    */
  def split(residualQuantity: Long): (LimitAskOrder, LimitAskOrder) = {
    val filledQuantity = quantity - residualQuantity
    (this.copy(quantity = filledQuantity), this.copy(quantity = residualQuantity))
  }
}
