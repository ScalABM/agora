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
package markets

import markets.orderbooks.mutable
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable


class SellerPostedPriceMarket(tradable: Tradable) {

  /** Used by sellers to cancel an outstanding ask order. */
  def cancel(order: AskOrder): Option[AskOrder] = askOrderBook.remove(order.uuid)

  def fill(order: BidOrder): Option[Iterable[Fill]] = {
    askOrderBook.find(askOrder => askOrder.price <= order.price) match {
      case Some(askOrder) =>
        askOrderBook.remove(askOrder.uuid)  // SIDE EFFECT!
        val price = askOrder.price  // price formation (plugin!)
        val excessDemand = order.quantity - askOrder.quantity  // quantity determination (plugin!)

        // rationing is possible! (probably should be done at the level of individual order)
        if (excessDemand > 0) {
          val (_, residualBidOrder) = order.split(excessDemand)
          val fills = List(Fill(askOrder, order, price, askOrder.quantity, None, Some(residualBidOrder), timestamp = ???, uuid = ???))
          Some(fills)
        } else if(excessDemand < 0) {
          val (_, residualAskOrder) = askOrder.split(-excessDemand)
          askOrderBook.add(residualAskOrder)  // SIDE EFFECT!
          val fills = List(Fill(askOrder, order, price, order.quantity, Some(residualAskOrder), None, timestamp = ???, uuid = ???))
          Some(fills)
        } else {
          val fills = List(Fill(askOrder, order, price, order.quantity, None, None, timestamp = ???, uuid = ???))
          Some(fills)
        }

      case None => None
    }
  }

  /** Used by sellers to place their ask orders into the market. */
  def place(order: AskOrder): Unit = askOrderBook.add(order)

  /* protected at the package level for testing purposes. */
  protected[markets] val askOrderBook = mutable.OrderBook[AskOrder](tradable)

}
