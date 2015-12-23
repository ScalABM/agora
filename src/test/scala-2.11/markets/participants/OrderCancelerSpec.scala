package markets.participants

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestProbe, TestActorRef, TestKit}

import java.util.UUID

import markets.{Cancel, Accepted}
import markets.orders.limit.LimitAskOrder
import markets.tickers.Tick
import markets.tradables.TestTradable
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpecLike}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random


class OrderCancelerSpec extends TestKit(ActorSystem("OrderCancelerSpec"))
  with FeatureSpecLike
  with GivenWhenThen
  with Matchers {

  /** Shutdown TestSystem after running tests. */
  def afterAll(): Unit = {
    system.terminate()
  }

  def timestamp(): Long = {
    System.currentTimeMillis()
  }

  def uuid(): UUID = {
    UUID.randomUUID()
  }

  feature("A OrderCanceler should be able to schedule SubmitOrderCancellation messages.") {

    val market = TestProbe()
    val prng = new Random(42)
    val ticker = Agent(Tick(1, 1, Some(1), 1, 1))
    val tradable = TestTradable("GOOG")

    scenario("A OrderCanceler schedules the future cancellation of an order.") {
      val orderCancelerProps = TestOrderCanceler.props(market.ref, prng, ticker, tradable)
      val orderCancelerRef = TestActorRef[OrderCanceler](orderCancelerProps)
      val orderCancelerActor = orderCancelerRef.underlyingActor

      val initialDelay = 10.millis
      orderCancelerActor.scheduleOrderCancellation(system.scheduler, initialDelay)

      When("An OrderCanceler has no outstanding orders...")

      Then("...the market should not receive any message.")
      market.expectNoMsg()

      When("An OrderCanceler has some outstanding orders...")
      val order = LimitAskOrder(orderCancelerRef, 10, 100, timestamp(), tradable, uuid())
      val accepted = Accepted(order, timestamp(), uuid())
      orderCancelerRef ! accepted

      orderCancelerActor.scheduleOrderCancellation(system.scheduler, initialDelay)

      Then("...the market should receive a Cancel message.")
      val timeout = initialDelay + 50.millis  // @todo is this the best way to test?
      within(initialDelay, timeout) {
        market.expectMsgAnyClassOf(classOf[Cancel])
      }

    }
  }
}
