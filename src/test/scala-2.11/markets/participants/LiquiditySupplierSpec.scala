package markets.participants

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestProbe, TestActorRef, TestKit}

import markets.orders.limit.LimitOrderLike
import markets.tickers.Tick
import markets.tradables.TestTradable
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpecLike}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random


class LiquiditySupplierSpec extends TestKit(ActorSystem("LiquiditySupplierSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  feature("A LiquiditySupplier should be able to schedule SubmitMarketOrder messages.") {

    val market = TestProbe()
    val prng = new Random(42)
    val ticker = Agent(Tick(1, 1, Some(1), 1, 1))
    val tradable = TestTradable("GOOG")

    scenario("A LiquiditySupplier schedules the future submission of limit orders.") {
      val liquiditySupplierProps = TestLiquiditySupplier.props(market.ref, prng, ticker, tradable)
      val liquiditySupplierRef = TestActorRef[LiquiditySupplier](liquiditySupplierProps)
      val liquiditySupplierActor = liquiditySupplierRef.underlyingActor

      When("a LiquitidySupplier schedules the submission of a single limit order...")
      val initialDelay = 10.millis
      liquiditySupplierActor.scheduleLimitOrder(system.scheduler, initialDelay)

      Then("...the market should receive a single limit order.")

      val timeout = initialDelay + 50.millis  // @todo is this the best way to test?
      within(initialDelay, timeout) {
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
      }

      When("a LiquitidySupplier schedules the repeated submission of limit orders...")
      val interval = 5.millis
      liquiditySupplierActor.scheduleLimitOrder(system.scheduler, initialDelay, interval)

      Then("...the market should receive repeated limit orders.")

      within(initialDelay, timeout) {  // @todo must be a better way to test this!
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
        market.expectMsgAnyClassOf(classOf[LimitOrderLike])
      }
    }
  }
}
