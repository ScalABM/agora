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
package markets.actors.participants.strategies.trading

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


/** Stub implementation of an `TradingStrategy` for testing purposes.
  *
  * @param price desired price for an `Order`.
  * @param quantity desired quantity for the `Order`.
  * @tparam T either `AskOrder` or `BidOrder`, depending on whether `TradingStrategy` is used to
  *           issue `AskOrder` or `BidOrder`.
  * @note If `price` is `Some(limitPrice)`, then `LimitOrder` will be issued; otherwise if `price`
  *       is `None`, then `MarketOrder` will be issued. If `quantity` is `0`, then strategy
  *       returns `None`: setting `quantity=0` is used to mimic the behavior of an infeasible
  *       trading strategy.
  */
class TestTradingStrategy[T <: Order](val price: Option[Long],
                                      val quantity: Long)
  extends TradingStrategy[T]
  with ConstantPrice[T]
  with ConstantQuantity[T] {

  def apply(tradable: Tradable, ticker: Agent[Tick]): Option[(Option[Long], Long)] = {
    if (quantity == 0) None else Some(price, quantity)
  }

}


object TestTradingStrategy {

  def apply[T <: Order](price: Option[Long],
                        quantity: Long): TestTradingStrategy[T] = {
    new TestTradingStrategy[T](price, quantity)
  }

  def apply[T <: Order](quantity: Long): TestTradingStrategy[T] = {
    new TestTradingStrategy[T](None, quantity)
  }

}