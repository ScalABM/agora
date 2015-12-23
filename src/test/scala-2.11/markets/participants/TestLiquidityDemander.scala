package markets.participants

import akka.actor.{Props, ActorRef}
import akka.agent.Agent

import markets.orders.Order
import markets.orders.market.{MarketBidOrder, MarketAskOrder, MarketOrderLike}
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.mutable
import scala.util.Random


class TestLiquidityDemander(market: ActorRef,
                            prng: Random,
                            ticker: Agent[Tick],
                            tradable: Tradable) extends LiquidityDemander {

  val markets = mutable.Map(tradable -> market)

  val outstandingOrders = mutable.Set.empty[Order]

  val tickers = mutable.Map(tradable -> ticker)

  def generateMarketOrder(): MarketOrderLike = {
    if (prng.nextDouble() < 0.5) {
      MarketAskOrder(self, 1, timestamp(), tradable, uuid())
    } else {
      MarketBidOrder(self, 1, timestamp(), tradable, uuid())
    }

  }

  def submitMarketOrder(): Unit = {
    market tell(generateMarketOrder(), self)
  }

}


object TestLiquidityDemander {

  def props(market: ActorRef,
            prng: Random,
            ticker: Agent[Tick],
            tradable: Tradable): Props = {
    Props(new TestLiquidityDemander(market, prng, ticker, tradable))
  }
}