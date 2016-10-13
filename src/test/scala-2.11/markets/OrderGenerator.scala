package markets

import markets.tradables.TestTradable
import org.apache.commons.math3.{distribution, random}


/** Mixin trait providing an `OrderGenerator` for testing purposes. */
trait OrderGenerator {

  val invalidTradable = TestTradable()

  val validTradable = TestTradable()

  val orderGenerator = {

    // specify the seed
    val seed = 42
    val prng = new random.MersenneTwister(seed)

    // specify the sampling distribution for prices
    val (minPrice, maxPrice) = (1, 200)
    val priceDistribution = new distribution.UniformRealDistribution(prng, minPrice, maxPrice)

    // specify the sampling distribution for quantities
    val (maxQuantity, exponent) = (1000000, 1.0)
    val quantityDistribution = new distribution.ZipfDistribution(prng, maxQuantity, exponent)

    RandomOrderGenerator(priceDistribution, quantityDistribution)

  }

}
