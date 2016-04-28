package markets.actors

import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.engines.mutable.TestMutableMatchingEngine
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable


case class TestMutableMarketActor(settlementMechanism: ActorRef,
                                  ticker: Agent[Tick],
                                  tradable: Tradable)
  extends GenericMarketActor[mutable.Set[AskOrder], mutable.Set[BidOrder]] {

  val matchingEngine = new TestMutableMatchingEngine

}


object TestMutableMarketActor {

  def props(settlementMechanism: ActorRef,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(TestMutableMarketActor(settlementMechanism, ticker, tradable))
  }
}
