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

import markets.orders.limit.{LimitBidOrder, LimitAskOrder}
import markets.tradables.Tradable

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext


/** Mixin Trait providing behavior necessary to generate `LimitOrderLike` orders. */
trait LiquiditySupplier extends MarketParticipant {

  def limitAskOrderStrategy: LimitOrderTradingStrategy

  def limitBidOrderStrategy: LimitOrderTradingStrategy

  private final def generateLimitAskOrder(price: Long, quantity: Long, tradable: Tradable) = {
    LimitAskOrder(self, price, quantity, timestamp(), tradable, uuid())
  }

  private final def generateLimitBidOrder(price: Long, quantity: Long, tradable: Tradable) = {
    LimitBidOrder(self, price, quantity, timestamp(), tradable, uuid())
  }

  /** Schedule a limit order.
    *
    * @param scheduler
    * @param initialDelay
    * @param executionContext
    */
  protected final def scheduleLimitOrder(scheduler: Scheduler,
                                         initialDelay: FiniteDuration,
                                         message: SubmitLimitOrder)
                                        (implicit executionContext: ExecutionContext): Unit = {
    scheduler.scheduleOnce(initialDelay, self, message)(executionContext)
  }

  /** Schedule a limit order.
    *
    * @param scheduler
    * @param initialDelay
    * @param interval
    * @param executionContext
    */
  protected final def scheduleLimitOrder(scheduler: Scheduler,
                                         initialDelay: FiniteDuration,
                                         interval: FiniteDuration,
                                         message: SubmitLimitOrder)
                                        (implicit executionContext: ExecutionContext): Unit = {
    scheduler.schedule(initialDelay, interval, self, message)(executionContext)
  }

  override def receive: Receive = {
    case SubmitLimitAskOrder =>
      val (price, quantity, tradable) = limitAskOrderStrategy.execute()
      val limitAskOrder = generateLimitAskOrder(price, quantity, tradable)
      submit(limitAskOrder)
    case SubmitLimitBidOrder =>
      val (price, quantity, tradable) = limitBidOrderStrategy.execute()
      val limitBidOrder = generateLimitBidOrder(price, quantity, tradable)
      submit(limitBidOrder)
    case message => super.receive(message)
  }

}
