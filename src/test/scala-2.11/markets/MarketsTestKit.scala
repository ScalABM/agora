package markets

import java.util.UUID

import scala.util.Random


trait MarketsTestKit {

  /** Generates a timestamp. */
  def timestamp(): Long = {
    System.currentTimeMillis()
  }

  /** Generates a UUID. */
  def uuid(): UUID = {
    UUID.randomUUID()
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
  private[this] def nextLong(prng: Random, lower: Long, upper: Long) = {
    math.abs(prng.nextLong()) % (upper - lower) + lower
  }
}
