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

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


/** Trait that handles order placement and cancellation. */
case class OrderPlacementStrategy(scheduler: Scheduler) {

  /** Schedule a specific message to be sent to some market (possibly after some delay).
    *
    * @param delay
    * @param market
    * @param message
    * @param executionContext
    */
  def scheduleOnce(delay: FiniteDuration,
                   market: ActorRef,
                   message: Any)
                  (implicit executionContext: ExecutionContext): Unit = {
    scheduler.scheduleOnce(delay, market, message)(executionContext)
  }

  /** Schedule a specific message to be sent repeatedly to some market participant (possibly
    * after some delay).
    *
    * @param initialDelay
    * @param interval
    * @param participant
    * @param reminder
    * @param executionContext
    */
  def schedule(initialDelay: FiniteDuration,
               interval: FiniteDuration,
               participant: ActorRef,
               reminder: Reminder)
              (implicit executionContext: ExecutionContext): Unit = {
    scheduler.schedule(initialDelay, interval, participant, reminder)(executionContext)
  }
}
