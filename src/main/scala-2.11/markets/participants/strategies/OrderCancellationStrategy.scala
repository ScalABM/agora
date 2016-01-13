package markets.participants.strategies

import akka.actor.{ActorRef, Scheduler}

import markets.orders.Order

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


trait OrderCancellationStrategy {

  def scheduler: Scheduler

  def cancelOneOf[T <: mutable.Iterable[Order]](outstandingOrders: T): Option[Order]

  /** Schedule a specific message to be sent to some market (possibly after some delay).
    *
    * @param delay
    * @param receiver
    * @param message
    * @param executionContext
    */
  def scheduleOnce(delay: FiniteDuration,
                   receiver: ActorRef,
                   message: Any)
                  (implicit executionContext: ExecutionContext): Unit = {
    scheduler.scheduleOnce(delay, receiver, message)(executionContext)
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
               reminder: Any)
              (implicit executionContext: ExecutionContext): Unit = {
    scheduler.schedule(initialDelay, interval, participant, reminder)(executionContext)
  }

}
