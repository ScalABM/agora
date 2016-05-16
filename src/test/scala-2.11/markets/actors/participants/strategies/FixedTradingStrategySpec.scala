package markets.actors.participants.strategies

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global


class FixedTradingStrategySpec extends FlatSpec with Matchers {

  val tradable = Tradable("GOOG")
  val tickers = Map[Tradable, Agent[Tick]](tradable -> Agent(Tick(1, 1, 1, 1, 1)))
  val quantity = 1000

  "A FixedTradingStrategy" should "generate strategies for limit orders." in {
    val limitPrice = 100
    val strategy = FixedTradingStrategy(Some(limitPrice), quantity)
    val expectedStrategy = (Some(limitPrice), quantity, tradable)

    strategy.askOrderStrategy(tickers) should be(Some(expectedStrategy))
    strategy.bidOrderStrategy(tickers) should be(Some(expectedStrategy))

  }

  "A FixedTradingStrategy" should "generate strategies for market orders." in {
    val strategy = FixedTradingStrategy(None, quantity)
    val expectedStrategy = (None, quantity, tradable)

    strategy.askOrderStrategy(tickers) should be(Some(expectedStrategy))
    strategy.bidOrderStrategy(tickers) should be(Some(expectedStrategy))

  }

  "If no tradables available, then a FixedTradingStrategy" should "not generate any strategies." in {
    val strategy = FixedTradingStrategy(None, quantity)
    val emptyTickers = tickers.empty

    strategy.askOrderStrategy(emptyTickers) should be(None)
    strategy.bidOrderStrategy(emptyTickers) should be(None)

  }
}
