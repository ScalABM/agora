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

import markets.Cancel
import markets.orders.Order

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext


/** Mixin Trait providing behavior necessary to cancel outstanding orders. */
trait OrderCanceler extends MarketParticipant {

  def orderCancellationStrategy(): Option[Order]

  override def receive: Receive = {
    case SubmitOrderCancellation =>
      orderCancellationStrategy() match {
        case Some(order) =>
          val orderCancellation = cancel(order)
          submit(orderCancellation)
        case None =>  // no outstanding orders to cancel!
      }
    case message => super.receive(message)
  }

  /** Schedule order cancellation.
    *
    * @param scheduler
    * @param initialDelay
    * @param executionContext
    */
  protected final def scheduleOrderCancellation(scheduler: Scheduler,
                                                initialDelay: FiniteDuration)
                                               (implicit executionContext: ExecutionContext): Unit = {
    scheduler.scheduleOnce(initialDelay, self, SubmitOrderCancellation)(executionContext)
  }

  /** Schedule order cancellation.
    *
    * @param scheduler
    * @param initialDelay
    * @param interval
    * @param executionContext
    */
  protected final def scheduleOrderCancellation(scheduler: Scheduler,
                                                initialDelay: FiniteDuration,
                                                interval: FiniteDuration)
                                               (implicit executionContext: ExecutionContext): Unit = {
    scheduler.schedule(initialDelay, interval, self, SubmitOrderCancellation)(executionContext)
  }

  /** Submit an order cancellation to a market. */
  private def submit(cancellation: Cancel): Unit = {
    val market = markets(cancellation.order.tradable)
    market tell(cancellation, self)
  }

  /** Generate an order cancellation message. */
  private def cancel(order: Order): Cancel = {
    Cancel(order, timestamp(), uuid())
  }

  private object SubmitOrderCancellation

}
