package markets

import java.util.UUID

import markets.orders.{AskOrder, BidOrder, Order}
import markets.orders.limit.{LimitAskOrder, LimitBidOrder}
import markets.orders.market.{MarketAskOrder, MarketBidOrder}
import markets.tradables.Security

import scala.util.Random


/** Object used to generator random orders of various types. */
object RandomOrderGenerator {

  /** Generates a UUID. */
  def uuid(): UUID = {
    UUID.randomUUID()
  }

  /** Generate a random `AskOrder`.
    *
    * @param prng pseudo-random number generator.
    * @param marketOrderProbability probability of generating a `MarketAskOrder`.
    * @param minimumPrice lower bound on the price for a `LimitAskOrder`.
    * @param maximumPrice upper bound on the price for a `LimitAskOrder`.
    * @param minimumQuantity lower bound on the `AskOrder` quantity.
    * @param maximumQuantity upper bound on the `AskOrder` quantity.
    * @param timestamp a timestamp for the `AskOrder`.
    * @param tradable the `AskOrder` validTradable.
    * @return either `LimitAskOrder` or `MarketAskOrder`, depending.
    */
  def randomAskOrder(prng: Random,
                     marketOrderProbability: Double = 0.5,
                     minimumPrice: Long = 1,
                     maximumPrice: Long = Long.MaxValue,
                     minimumQuantity: Long = 1,
                     maximumQuantity: Long = Long.MaxValue,
                     timestamp: Long = 1,
                     tradable: Security): AskOrder = {
    val quantity = randomQuantity(prng, minimumQuantity, maximumQuantity)
    if (prng.nextDouble() <= marketOrderProbability) {
      MarketAskOrder(uuid(), quantity, timestamp, tradable, uuid())
    } else {
      val limitPrice = randomLimitPrice(prng, minimumPrice, maximumPrice)
      LimitAskOrder(uuid(), limitPrice, quantity, timestamp, tradable, uuid())
    }
  }

  /** Generate a random `BidOrder`.
    *
    * @param prng pseudo-random number generator.
    * @param marketOrderProbability probability of generating a `MarketBidOrder`.
    * @param minimumPrice lower bound on the price for a `LimitBidOrder`.
    * @param maximumPrice upper bound on the price for a `LimitBidOrder`.
    * @param minimumQuantity lower bound on the `BidOrder` quantity.
    * @param maximumQuantity upper bound on the `BidOrder` quantity.
    * @param timestamp a timestamp for the `Bidrder`.
    * @param tradable the `BidOrder` validTradable.
    * @return either `LimitBidOrder` or `MarketBidOrder`, depending.
    */
  def randomBidOrder(prng: Random,
                     marketOrderProbability: Double = 0.5,
                     minimumPrice: Long = 1,
                     maximumPrice: Long = Long.MaxValue,
                     minimumQuantity: Long = 1,
                     maximumQuantity: Long = Long.MaxValue,
                     timestamp: Long = 1,
                     tradable: Security): BidOrder = {
    val quantity = randomQuantity(prng, minimumQuantity, maximumQuantity)
    if (prng.nextDouble() <= marketOrderProbability) {
      MarketBidOrder(uuid(), quantity, timestamp, tradable, uuid())
    } else {
      val limitPrice = randomLimitPrice(prng, minimumPrice, maximumPrice)
      LimitBidOrder(uuid(), limitPrice, quantity, timestamp, tradable, uuid())
    }
  }

  /* Returns a randomly generated limit price between lower and upper. */
  private[this] def randomLimitPrice(prng: Random, lower: Long, upper: Long): Long = {
    nextLong(prng, lower, upper)
  }

  /* Returns a randomly generated quantity between lower and upper. */
  private[this] def randomQuantity(prng: Random, lower: Long, upper: Long): Long = {
    nextLong(prng, lower, upper)
  }

  /* Returns a randomly generated Long integer between some lower and upper bound. */
  private[this] def nextLong(prng: Random, lower: Long = 1, upper: Long = Long.MaxValue) = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }

}
