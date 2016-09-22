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
package markets.orders.market

import java.util.UUID

import markets.orders.AskOrder
import markets.tradables.Security


case class MarketAskOrder(issuer: UUID,
                          quantity: Long,
                          timestamp: Long,
                          tradable: Security,
                          uuid: UUID) extends MarketOrder with AskOrder {

  val price: Long = 0

  def split(residualQuantity: Long): (MarketAskOrder, MarketAskOrder) = {
    val filledQuantity = quantity - residualQuantity
    (this.copy(quantity = filledQuantity), this.copy(quantity = residualQuantity))
  }

}