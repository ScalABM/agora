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

import akka.actor.{ActorRef, Scheduler}

import markets.orders.Order

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


class OrderPlacementStrategy {

  /** Schedule order placement.
    *
    * @param scheduler
    * @param delay
    * @param market
    * @param order
    * @param executionContext
    */
  def scheduleOnce(scheduler: Scheduler,
                   delay: FiniteDuration,
                   market: ActorRef,
                   order: Order)
                  (implicit executionContext: ExecutionContext): Unit = {
    scheduler.scheduleOnce(delay, market, order)(executionContext)
  }

  /** Schedule a repeated order placement.
    *
    * @param scheduler
    * @param initialDelay
    * @param interval
    * @param participant
    * @param reminder
    * @param executionContext
    */
  def schedule(scheduler: Scheduler,
               initialDelay: FiniteDuration,
               interval: FiniteDuration,
               participant: ActorRef,
               reminder: SubmitOrder)
              (implicit executionContext: ExecutionContext): Unit = {
    scheduler.schedule(initialDelay, interval, participant, reminder)(executionContext)
  }
}
