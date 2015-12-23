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

import akka.actor.Scheduler

import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.Tradable

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


/** A Trait providing behavior necessary to submit `MarketOrderLike` orders. */
trait LiquidityDemander extends MarketParticipant {

  def marketAskOrderStrategy: MarketOrderTradingStrategy

  def marketBidOrderStrategy: MarketOrderTradingStrategy

  private final def generateMarketAskOrder(quantity: Long, tradable: Tradable) = {
    MarketAskOrder(self, quantity, timestamp(), tradable, uuid())
  }

  private final def generateMarketBidOrder(quantity: Long, tradable: Tradable) = {
    MarketBidOrder(self, quantity, timestamp(), tradable, uuid())
  }

  /** Schedule a market order.
    *
    * @param scheduler
    * @param initialDelay
    * @param executionContext
    */
  protected final def scheduleMarketOrder(scheduler: Scheduler,
                                          initialDelay: FiniteDuration,
                                          message: SubmitMarketOrder)
                                         (implicit executionContext: ExecutionContext): Unit = {
    scheduler.scheduleOnce(initialDelay, self, message)(executionContext)
  }

  /** Schedule a market order.
    *
    * @param scheduler
    * @param initialDelay
    * @param interval
    * @param executionContext
    */
  protected final def scheduleMarketOrder(scheduler: Scheduler,
                                          initialDelay: FiniteDuration,
                                          interval: FiniteDuration,
                                          message: SubmitMarketOrder)
                                         (implicit executionContext: ExecutionContext): Unit = {
    scheduler.schedule(initialDelay, interval, self, message)(executionContext)
  }

  override def receive: Receive = {
    case SubmitMarketAskOrder =>
      val (quantity, tradable) = marketAskOrderStrategy.execute()
      val marketAskOrder = generateMarketAskOrder(quantity, tradable)
      submit(marketAskOrder)
    case SubmitMarketBidOrder =>
      val (quantity, tradable) = marketBidOrderStrategy.execute()
      val marketBidOrder = generateMarketBidOrder(quantity, tradable)
      submit(marketBidOrder)
    case message => super.receive(message)
  }

}

