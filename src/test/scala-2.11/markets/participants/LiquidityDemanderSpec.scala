package markets.participants

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestProbe, TestActorRef, TestKit}

import markets.orders.market.MarketOrderLike
import markets.tickers.Tick
import markets.tradables.TestTradable
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpecLike}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random


class LiquidityDemanderSpec extends TestKit(ActorSystem("LiquidityDemanderSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  feature("A LiquidityDemander should be able to schedule SubmitMarketOrder messages.") {

    val market = TestProbe()
    val prng = new Random(42)
    val ticker = Agent(Tick(1, 1, Some(1), 1, 1))
    val tradable = TestTradable("GOOG")

    scenario("A LiquidityDemander schedules the future submission of a market order.") {
      val liquidityDemanderProps = TestLiquidityDemander.props(market.ref, prng, ticker, tradable)
      val liquidityDemanderRef = TestActorRef[LiquidityDemander](liquidityDemanderProps)
      val liquidityDemanderActor = liquidityDemanderRef.underlyingActor

      val initialDelay = 10.millis
      liquidityDemanderActor.scheduleMarketOrder(system.scheduler, initialDelay)

      Then("...the market should receive a single market order.")

      val timeout = initialDelay + 50.millis  // @todo is this the best way to test?
      within(initialDelay, timeout) {
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
      }
      
      When("a LiquitidySupplier schedules the repeated submission of market orders...")
      val interval = 5.millis
      liquidityDemanderActor.scheduleMarketOrder(system.scheduler, initialDelay, interval)

      Then("...the market should receive repeated market orders.")

      within(initialDelay, timeout) {  // @todo must be a better way to test this!
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
        market.expectMsgAnyClassOf(classOf[MarketOrderLike])
      }
    }
  }
}
