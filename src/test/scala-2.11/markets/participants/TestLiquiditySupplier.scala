package markets.participants

import akka.actor.{Props, ActorRef}
import akka.agent.Agent

import markets.orders.Order
import markets.orders.limit.{LimitBidOrder, LimitAskOrder, LimitOrderLike}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Random


class TestLiquiditySupplier(market: ActorRef,
                            prng: Random,
                            ticker: Agent[Tick],
                            tradable: Tradable) extends LiquiditySupplier {

  val markets = mutable.Map(tradable -> market)

  val outstandingOrders = mutable.Set.empty[Order]

  val tickers = mutable.Map(tradable -> ticker)

  def generateLimitOrder(): LimitOrderLike = {
    if (prng.nextDouble() < 0.5) {
      LimitAskOrder(self, 1, 1, timestamp(), tradable, uuid())
    } else {
      LimitBidOrder(self, 1, 1, timestamp(), tradable, uuid())
    }
  }

  def submitLimitOrder(): Unit = {
    market tell(generateLimitOrder(), self)
  }

}


object TestLiquiditySupplier {

  def props(market: ActorRef,
            prng: Random,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(new TestLiquiditySupplier(market, prng, ticker, tradable))
  }
}