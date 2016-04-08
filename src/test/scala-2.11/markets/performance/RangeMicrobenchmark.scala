package markets.performance

import markets.orders.limit.{LimitAskOrder, LimitBidOrder, LimitOrderLike}
import markets.orders.market.{MarketAskOrder, MarketBidOrder, MarketOrderLike}
import org.scalameter.api._

import scala.util.Random


object RangeMicroBenchmark extends Bench.OnlineRegressionReport {

  val askOrderProb = 0.5




  // define input data for benchmark tests...this would need to be a collection of orders...
  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val ranges: Gen[Range] = for {
    size <- sizes
  } yield 0 until size

  performance of "Range" in {
    // nested tests
  }

  performance of "Range" in {
    measure method "map" in {
      using(ranges) in {
        r => r.map(_ + 1)
      }
    }
  }

}
