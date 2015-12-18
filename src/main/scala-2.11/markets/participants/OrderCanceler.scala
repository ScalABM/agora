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

import markets.orders.Order
import markets.Cancel

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext


trait OrderCanceler extends MarketParticipant {

  def generateOrderCancellation(orders: mutable.Set[Order]): Option[Cancel]

  def submitOrderCancellation(): Unit

  def scheduleOrderCancellation(initialDelay: FiniteDuration)
                               (implicit executionContext: ExecutionContext): Unit = {
    context.system.scheduler.scheduleOnce(initialDelay, self, SubmitOrderCancellation)(executionContext)
  }

  def scheduleOrderCancellation(initialDelay: FiniteDuration,
                                interval: FiniteDuration)
                               (implicit executionContext: ExecutionContext): Unit = {
    context.system.scheduler.schedule(initialDelay, interval, self, SubmitOrderCancellation)(executionContext)
  }

  override def receive: Receive = {
    case SubmitOrderCancellation => submitOrderCancellation()
    case message => super.receive(message)
  }

  private object SubmitOrderCancellation

}
