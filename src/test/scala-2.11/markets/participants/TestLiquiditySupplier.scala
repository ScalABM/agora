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
package markets.participants

import akka.actor.{Props, ActorRef}
import akka.agent.Agent

import markets.orders.Order
import markets.participants.strategies.FixedLimitOrderTradingStrategy
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global


case class TestLiquiditySupplier(initialDelay: FiniteDuration,
                                 interval: Option[FiniteDuration],
                                 markets: mutable.Map[Tradable, ActorRef],
                                 tickers: mutable.Map[Tradable, Agent[Tick]])
  extends LiquiditySupplier[FixedLimitOrderTradingStrategy] {

  interval match {
    case Some(duration) =>
      context.system.scheduler.schedule(initialDelay, duration, self, SubmitLimitAskOrder)
    case None =>
      context.system.scheduler.scheduleOnce(initialDelay, self, SubmitLimitAskOrder)
  }

  val outstandingOrders = mutable.Set.empty[Order]

  val limitOrderTradingStrategy = new FixedLimitOrderTradingStrategy(1, 1)

}


object TestLiquiditySupplier {

  def props(initialDelay: FiniteDuration,
            interval: Option[FiniteDuration],
            markets: mutable.Map[Tradable, ActorRef],
            tickers: mutable.Map[Tradable, Agent[Tick]]): Props = {
    Props(new TestLiquiditySupplier(initialDelay, interval, markets, tickers))
  }
}