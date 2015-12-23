/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.participants

import akka.actor.{Props, ActorRef}
import akka.agent.Agent

import markets.orders.Order
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Random


class TestLiquiditySupplier(market: ActorRef,
                            prng: Random,
                            ticker: Agent[Tick],
                            tradable: Tradable) extends LiquiditySupplier {

  val markets = mutable.Map(tradable -> market)

  val outstandingOrders = mutable.Set.empty[Order]

  val tickers = mutable.Map(tradable -> ticker)

  def generateLimitOrder(): Order = {
    if (prng.nextDouble() < 0.5) {
      LimitAskOrder(self, 1, 1, timestamp(), tradable, uuid())
    } else {
      LimitBidOrder(self, 1, 1, timestamp(), tradable, uuid())
    }
  }

}


object TestLiquiditySupplier {

  def props(market: ActorRef,
            prng: Random,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(new TestLiquiditySupplier(market, prng, ticker, tradable))
  }
}