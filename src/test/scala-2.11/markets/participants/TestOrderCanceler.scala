package markets.participants

import akka.actor.{Props, ActorRef}
import akka.agent.Agent

import markets.Cancel
import markets.orders.Order
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Random


class TestOrderCanceler(market: ActorRef,
                        prng: Random,
                        ticker: Agent[Tick],
                        tradable: Tradable) extends OrderCanceler {

  val markets = mutable.Map(tradable -> market)

  val outstandingOrders = mutable.Set.empty[Order]

  val tickers = mutable.Map(tradable -> ticker)

  def generateOrderCancellation(): Option[Cancel] = {
    outstandingOrders.headOption match {
      case Some(order) =>
        Some(Cancel(order, timestamp(), uuid()))
      case None =>
        None
    }
  }

  def submitOrderCancellation(): Unit = {
    generateOrderCancellation() match {
      case Some(cancellation) =>
        market tell(cancellation, self)
      case None =>  // do nothing!
    }
  }

}


object TestOrderCanceler {

  def props(market: ActorRef,
            prng: Random,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(new TestOrderCanceler(market, prng, ticker, tradable))
  }
}