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

import java.util.UUID

import markets.matching.MatchingFunction
import markets.orderbooks
import markets.pricing.PricingFunction
import markets.tradables.orders.ask.AskOrder
import markets.tradables.orders.bid.BidOrder

import scala.collection.GenMap


trait DoubleAuction {

  def askOrderBook: orderbooks.OrderBook[AskOrder, GenMap[UUID, AskOrder]]

  def bidOrderBook: orderbooks.OrderBook[BidOrder, GenMap[UUID, BidOrder]]

  def cancel(order: AskOrder): Option[AskOrder] = askOrderBook.remove(order.uuid)

  def cancel(order: BidOrder): Option[BidOrder] = bidOrderBook.remove(order.uuid)

  def fill(order: AskOrder): Option[???]

  def fill(order: BidOrder): Option[???]

  def place(order: AskOrder): Unit = askOrderBook.add(order)

  def place(order: BidOrder): Unit = bidOrderBook.add(order)

  def matchingFunction: MatchingFunction[AskOrder, BidOrder]

  def pricingFunction: PricingFunction[AskOrder, BidOrder]


}