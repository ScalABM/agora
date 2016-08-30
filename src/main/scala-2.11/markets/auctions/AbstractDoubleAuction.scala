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
package markets.auctions

import markets.orderbooks.AbstractOrderBook
import markets.orders.{AskOrder, BidOrder}


abstract class AbstractDoubleAuction {

  def cancel(order: AskOrder): Option[AskOrder] = askOrderBook.remove(order.uuid)

  def cancel(order: BidOrder): Option[BidOrder] = bidOrderBook.remove(order.uuid)

  protected[auctions] def askOrderBook: AbstractOrderBook[AskOrder]

  protected[auctions] def bidOrderBook: AbstractOrderBook[BidOrder]

}
