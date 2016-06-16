package markets

import java.util.UUID

import markets.engines.orderbooks.OrderBook
import markets.orders.{AskOrder, BidOrder}
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.Tradable

import scala.util.Random


trait MarketsTestKit {

  def prng: Random

  val invalidTradable = Tradable("APPL")

  val validTradable = Tradable("GOOG")

  /** Generates a timestamp. */
  def timestamp(): Long = {
    System.currentTimeMillis()
  }

  /** Generates a UUID. */
  def uuid(): UUID = {
    UUID.randomUUID()
  }

  /** Generate a random `AskOrder`.
    *
    * @param marketOrderProbability probability of generating a `MarketAskOrder`.
    * @param minimumPrice lower bound on the price for a `LimitAskOrder`.
    * @param maximumPrice upper bound on the price for a `LimitAskOrder`.
    * @param minimumQuantity lower bound on the `AskOrder` quantity.
    * @param maximumQuantity upper bound on the `AskOrder` quantity.
    * @param timestamp a timestamp for the `AskOrder`.
    * @param tradable the `AskOrder` validTradable.
    * @return either `LimitAskOrder` or `MarketAskOrder`, depending.
    */
  def randomAskOrder(marketOrderProbability: Double = 0.5,
                     minimumPrice: Long = 1,
                     maximumPrice: Long = Long.MaxValue,
                     minimumQuantity: Long = 1,
                     maximumQuantity: Long = Long.MaxValue,
                     timestamp: Long = 1,
                     tradable: Tradable): AskOrder = {
    val quantity = randomQuantity(minimumQuantity, maximumQuantity)
    if (prng.nextDouble() <= marketOrderProbability) {
      MarketAskOrder(uuid(), quantity, timestamp, tradable, uuid())
    } else {
      val limitPrice = randomLimitPrice(minimumPrice, maximumPrice)
      LimitAskOrder(uuid(), limitPrice, quantity, timestamp, tradable, uuid())
    }
  }

  /** Generate a random `BidOrder`.
    *
    * @param marketOrderProbability probability of generating a `MarketBidOrder`.
    * @param minimumPrice lower bound on the price for a `LimitBidOrder`.
    * @param maximumPrice upper bound on the price for a `LimitBidOrder`.
    * @param minimumQuantity lower bound on the `BidOrder` quantity.
    * @param maximumQuantity upper bound on the `BidOrder` quantity.
    * @param timestamp a timestamp for the `Bidrder`.
    * @param tradable the `BidOrder` validTradable.
    * @return either `LimitBidOrder` or `MarketBidOrder`, depending.
    */
  def randomBidOrder(marketOrderProbability: Double = 0.5,
                     minimumPrice: Long = 1,
                     maximumPrice: Long = Long.MaxValue,
                     minimumQuantity: Long = 1,
                     maximumQuantity: Long = Long.MaxValue,
                     timestamp: Long = 1,
                     tradable: Tradable): BidOrder = {
    val quantity = randomQuantity(minimumQuantity, maximumQuantity)
    if (prng.nextDouble() <= marketOrderProbability) {
      MarketBidOrder(uuid(), quantity, timestamp, tradable, uuid())
    } else {
      val limitPrice = randomLimitPrice(minimumPrice, maximumPrice)
      LimitBidOrder(uuid(), limitPrice, quantity, timestamp, tradable, uuid())
    }
  }

  /** Returns a randomly generated limit price
    *
    * @param lower
    * @param upper
    * @return
    */
  protected def randomLimitPrice(lower: Long = 1, upper: Long = Long.MaxValue): Long = {
    nextLong(lower, upper)
  }

  /** Returns a randomly generated quantity.
    *
    * @param lower
    * @param upper
    * @return
    */
  protected def randomQuantity(lower: Long = 1, upper: Long = Long.MaxValue): Long = {
    nextLong(lower, upper)
  }

  /* Returns a randomly generated Long integer between some lower and upper bound. */
  def nextLong(lower: Long = 1, upper: Long = Long.MaxValue) = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }

}
