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

import markets.participants.strategies.TestLimitOrderTradingStrategy
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.{immutable, mutable}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global


class TestLiquiditySupplier(initialDelay: FiniteDuration,
                            interval: Option[FiniteDuration],
                            markets: mutable.Map[Tradable, ActorRef],
                            tickers: mutable.Map[Tradable, Agent[immutable.Seq[Tick]]])
  extends TestMarketParticipant(markets, tickers)
  with LiquiditySupplier {

  val limitOrderTradingStrategy = new TestLimitOrderTradingStrategy

  interval match {
    case Some(duration) =>
      orderPlacementStrategy.schedule(initialDelay, duration, self, SubmitLimitAskOrder)
    case None =>
      orderPlacementStrategy.scheduleOnce(initialDelay, self, SubmitLimitAskOrder)
  }

}


object TestLiquiditySupplier {

  def props(initialDelay: FiniteDuration,
            interval: Option[FiniteDuration],
            markets: mutable.Map[Tradable, ActorRef],
            tickers: mutable.Map[Tradable, Agent[immutable.Seq[Tick]]]): Props = {
    Props(new TestLiquiditySupplier(initialDelay, interval, markets, tickers))
  }
}