/*
Copyright 2016 ScalABM

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.economicsl.agora

import org.economicsl.agora.markets.tradables.TestTradable
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

    RandomOrderGenerator(prng, priceDistribution, quantityDistribution)

  }

}
