package markets.exchanges

import akka.actor.ActorRef
import akka.agent.Agent

import markets.orders.Order
import markets.tickers.Tick
import markets.{BaseActor, Cancel, MarketActor}
import markets.clearing.engines.MatchingEngine
import markets.tradables.Tradable


/** Mixin Trait providing `ExchangeLike` behavior to some `BaseActor`.
  *
  * @note An `ExchangeLike` actor supervises a collection of `MarketLike` actors that have the
  *       same type of matching engine and share a common settlement mechanism.
  */
trait ExchangeLike {
  this: BaseActor =>

  /** The type of [[markets.clearing.engines.MatchingEngine `MatchingEngine`]] used by
    * each of the [[markets.MarketActor `MarketActor`]] supervised by the `ExchangeLikeActor`.
    */
  def matchingEngine: MatchingEngine

  /** The common settlement mechanism actor shared by the [[markets.MarketActor `MarketActor`]]
    * supervised by the `ExchangeLikeActor`.
    */
  def settlementMechanism: ActorRef

  def marketActorFactory(ticker: Agent[Tick], tradable: Tradable): ActorRef = {
    val marketProps = MarketActor.props(matchingEngine, settlementMechanism, ticker, tradable)
    context.actorOf(marketProps, tradable.symbol)
  }

  def exchangeActorBehavior: Receive = {
    case order: Order =>  // get (or create) a suitable market actor and forward the order...
      val market = context.child(order.tradable.symbol).getOrElse {
        val ticker = ???  // @todo create new symbol!
        marketActorFactory(ticker, order.tradable)
      }
      market forward order
    case message @ Cancel(order, _, _) =>
      val market = context.child(order.tradable.symbol).getOrElse {
        ???  // @todo should never happen !
      }
      market forward message
  }

}
