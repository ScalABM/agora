package markets.orders

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, TestProbe}

import markets.MarketsTestKit
import markets.participants.TestOrderIssuer
import markets.tradables.TestTradable
import org.scalameter.api._


object OrderMemoryTest extends Bench.OnlineRegressionReport
  with MarketsTestKit {
  //override def persistor = new SerializationPersistor
  override def measurer = new Executor.Measurer.MemoryFootprint

  val sizes = Gen.range("size")(1000000, 10000000, 1000000)

  val testKit = new TestKit(ActorSystem())

  performance of "MemoryFootprint" in {
    performance of "Order" in {
      using(sizes) config (
        exec.benchRuns -> 10,
        exec.independentSamples -> 2
        ) in { numberOrders =>
          for (i <- 1 to numberOrders) yield {
            TestOrder(testKit.testActor, 1, 1, timestamp(), TestTradable("GOOG"), uuid())
          }
        }
      testKit.system.terminate()
    }
  }
}

