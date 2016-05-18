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
package markets.actors.participants.strategies.investment

import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable


/** [[markets.actors.participants.strategies.investment.InvestmentStrategy `InvestmentStrategy`]]
  * that returns the same [[markets.tradables.Tradable `Tradable`]] every time.
 *
  * @param tradable some [[markets.tradables.Tradable `Tradable`]].
  * @tparam T either [[markets.orders.AskOrder `AskOrder`]] or
  *           [[markets.orders.BidOrder `BidOrder`]], depending.
  */
class TestInvestmentStrategy[T <: Order](val tradable: Tradable)
  extends InvestmentStrategy[T]
  with ConstantTradable[T] {

  def apply(information: Map[Tradable, Agent[Tick]]): Option[Tradable] = {
    if (information.keySet.contains(tradable)) Some(tradable) else None
  }

}


object TestInvestmentStrategy {

  def apply[T <: Order](tradable: Tradable): TestInvestmentStrategy[T] = {
    new TestInvestmentStrategy[T](tradable)
  }

}