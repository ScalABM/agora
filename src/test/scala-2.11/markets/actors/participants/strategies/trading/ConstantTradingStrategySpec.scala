package markets.actors.participants.strategies.trading

import akka.agent.Agent

import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.random.MersenneTwister
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global


class ConstantTradingStrategySpec extends FlatSpec with Matchers {

  val prng = new MersenneTwister()
  val tradable = Tradable("GOOG")
  val ticker = Agent(Tick(1, 1, 1, 1, 1))

  "A ConstantTradingStrategy" should "generate strategies for limit orders." in {
    val limitPrice = Some(prng.nextLong(Long.MaxValue))
    val quantity = prng.nextLong(Long.MaxValue)

    val testStrategy = ConstantTradingStrategy[AskOrder](limitPrice, quantity)
    val expectedResult = Some((limitPrice, quantity))

    testStrategy(tradable, ticker) should be(expectedResult)

  }

  "A ConstantTradingStrategy" should "generate strategies for market orders." in {
    val quantity = prng.nextLong(Long.MaxValue)
    val strategy = ConstantTradingStrategy[BidOrder](quantity)
    val expectedResult = (None, quantity)

    strategy(tradable, ticker) should be(Some(expectedResult))

  }

}
