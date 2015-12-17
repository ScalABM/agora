package markets

import akka.actor.ActorRef
import akka.agent.Agent

import markets.clearing.Fill
import markets.clearing.engines.MatchingEngine
import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable

/** Mixin Trait providing MarketLike behavior to some BaseActor. */
trait MarketLike {
  this: BaseActor =>

  def matchingEngine: MatchingEngine

  def settlementMechanism: ActorRef

  def ticker: Agent[Tick]

  def tradable: Tradable

  def marketActorBehavior: Receive = {
    case order: Order if order.tradable == tradable =>
      sender() ! Accepted(order, timestamp(), uuid())
      val result = matchingEngine.findMatch(order)
      result match {
        case Some(matchings) =>
          matchings.foreach { matching =>
            val fill = Fill.fromMatching(matching, timestamp(), uuid())
            val tick = Tick.fromFill(fill)
            ticker.send(tick)  // SIDE EFFECT!
            settlementMechanism ! fill
          }
        case None =>  // @todo notify sender that no matches were generated?
      }
    case order: Order if !(order.tradable == tradable) =>
      sender() ! Rejected(order, timestamp(), uuid())
    case Cancel(order, _, _) =>
      val result = matchingEngine.remove(order)
      result match {
        case Some(residualOrder) => // Case notify order successfully canceled
          sender() ! Canceled(residualOrder, timestamp(), uuid())
        case None =>  // @todo notify sender that order was not canceled!
      }
  }

}
