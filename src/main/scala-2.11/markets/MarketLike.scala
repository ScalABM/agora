package markets

import akka.actor.ActorRef

import markets.clearing.engines.MatchingEngine
import markets.orders.Order
import markets.tradables.Tradable

/** Mixin Trait providing MarketLike behavior to some BaseActor. */
trait MarketLike {
  this: BaseActor =>

  def clearingMechanism: ActorRef

  def tradable: Tradable

  def marketActorBehavior: Receive = {
    case order: Order if order.tradable == tradable =>
      clearingMechanism forward order
      sender() ! Accepted(order, timestamp(), uuid())
    case order: Order if !(order.tradable == tradable) =>
      sender() ! Rejected(order, timestamp(), uuid())
    case message : Cancel =>
      clearingMechanism forward message
  }

}
