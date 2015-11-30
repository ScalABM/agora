package markets.exchanges

import akka.actor.ActorRef

import markets.{BaseActor, MarketActor}
import markets.clearing.engines.MatchingEngineLike
import markets.tradables.Tradable


/** Base trait defining the behavior of an `ExchangeLike` actor.
  *
  * @note An `ExchangeLike` actor supervises a collection of `MarketLike` actors that have the
  *       same type of matching engine and share a common settlement mechanism.
  */
trait ExchangeLike {
  this: BaseActor =>

  /** The type of [[markets.clearing.engines.MatchingEngineLike `MatchingEngineLike`]] used by
    * each of the [[markets.MarketActor `MarketActor`]] supervised by the `ExchangeLikeActor`.
    */
  def matchingEngine: MatchingEngineLike

  /** The common settlement mechanism actor shared by the [[markets.MarketActor `MarketActor`]]
    * supervised by the `ExchangeLikeActor`.
    */
  def settlementMechanism: ActorRef

  def marketActorFactory(tradable: Tradable): ActorRef = {
    val marketProps = MarketActor.props(matchingEngine, settlementMechanism, tradable)
    context.actorOf(marketProps, tradable.ticker)
  }

}
