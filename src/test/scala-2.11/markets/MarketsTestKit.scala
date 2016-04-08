package markets

import akka.testkit.TestKit

import java.util.UUID

import markets.orders.limit.{LimitAskOrder, LimitBidOrder, LimitOrderLike}
import markets.orders.market.{MarketAskOrder, MarketBidOrder, MarketOrderLike}
import markets.tradables.Tradable

import scala.util.Random


trait MarketsTestKit {
  this: TestKit =>

  /** Generates a timestamp. */
  def timestamp(): Long = {
    System.currentTimeMillis()
  }

  /** Generates a UUID. */
  def uuid(): UUID = {
    UUID.randomUUID()
  }

  /** Generates a random limit order for some tradable.
    *
    * @param askOrderProb
    * @param prng
    * @param tradable
    * @return
    */
  def generateLimitOrder(askOrderProb: Float, prng: Random, tradable: Tradable): LimitOrderLike = {
    val price = randomLimitPrice(prng)
    val quantity = randomQuantity(prng)
    if (askOrderProb <= prng.nextFloat()) {
      LimitAskOrder(testActor, price, quantity, timestamp(), tradable, uuid())
    } else {
      LimitBidOrder(testActor, price, quantity, timestamp(), tradable, uuid())
    }
  }

  /** Generates a random market order.
    *
    * @param askOrderProb
    * @param prng
    * @param tradable
    * @return
    */
  def generateMarketOrder(askOrderProb: Float, prng: Random, tradable: Tradable): MarketOrderLike = {
    val quantity = randomQuantity(prng)
    if (askOrderProb <= prng.nextFloat()) {
      MarketAskOrder(testActor, quantity, timestamp(), tradable, uuid())
    } else {
      MarketBidOrder(testActor, quantity, timestamp(), tradable, uuid())
    }
  }

  /** Returns a randomly generated limit price
    *
    * @param prng
    * @param lower
    * @param upper
    * @return
    */
  def randomLimitPrice(prng: Random, lower: Long = 1, upper: Long = Long.MaxValue): Long = {
    nextLong(prng, lower, upper)
  }

  /** Returns a randomly generated quantity.
    * 
    * @param prng
    * @param lower
    * @param upper
    * @return
    */
  def randomQuantity(prng: Random, lower: Long = 1, upper: Long = Long.MaxValue): Long = {
    nextLong(prng, lower, upper)
  }

  /* Returns a randomly generated Long integer between some lower and upper bound. */
  private [this] def nextLong(prng: Random, lower: Long, upper: Long) = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }
}
