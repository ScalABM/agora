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
package markets

import java.util.UUID

import markets.tradables.orders.ask.{AskOrder, LimitAskOrder, MarketAskOrder}
import markets.tradables.orders.bid.{LimitBidOrder, MarketBidOrder}
import markets.tradables.Tradable

import org.apache.commons.math3.{distribution, random}


/** Class for generating random `Order` instances for testing purposes.
  *
  * @param priceDistribution sampling distribution for `price` values.
  * @param quantityDistribution sampling distribution for `quantity` values.
  */
case class RandomOrderGenerator(prng: random.RandomGenerator,
                                priceDistribution: distribution.RealDistribution,
                                quantityDistribution: distribution.IntegerDistribution) {

  /* Make static methods defined on the companion object accessible. */
  import RandomOrderGenerator._

  /** Generates a random `AskOrder` for a particular `Tradable`.
    *
    * @param marketOrderProbability the probability of a `MarketAskOrder`. Default is 0.5.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def randomAskOrder(marketOrderProbability: Double = 0.5, tradable: Tradable): AskOrder = {
    if (prng.nextDouble() < marketOrderProbability) randomLimitAskOrder(tradable) else randomMarketAskOrder(tradable)
  }

  /** Generates a random `LimitAskOrder` for a particular `Tradable`.
    *
    * @param price the limit price.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def randomLimitAskOrder(price: Long, tradable: Tradable): LimitAskOrder = {
    LimitAskOrder(randomIssuer(), price, randomQuantity(quantityDistribution), timestamp(), tradable, randomUUID())
  }

  /** Generates a random `LimitAskOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def randomLimitAskOrder(tradable: Tradable): LimitAskOrder = {
    LimitAskOrder(randomIssuer(), randomPrice(priceDistribution), randomQuantity(quantityDistribution),
      timestamp(), tradable, randomUUID())
  }

  /** Generates a random `LimitBidOrder` for a particular `Tradable`.
    *
    * @param price the limit price.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def randomLimitBidOrder(price: Long, tradable: Tradable): LimitBidOrder = {
    LimitBidOrder(randomIssuer(), price, randomQuantity(quantityDistribution), timestamp(), tradable, randomUUID())
  }

  /** Generates a random `LimitBidOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def randomLimitBidOrder(tradable: Tradable): LimitBidOrder = {
    LimitBidOrder(randomIssuer(), randomPrice(priceDistribution), randomQuantity(quantityDistribution),
      timestamp(), tradable, randomUUID())
  }

  /** Generates a random `MarketAskOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `MarketAskOrder`.
    */
  def randomMarketAskOrder(tradable: Tradable): MarketAskOrder = {
    MarketAskOrder(randomIssuer(), randomQuantity(quantityDistribution), timestamp(), tradable, randomUUID())
  }

  /** Generates a random `MarketBidOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `MarketBidOrder`.
    */
  def randomMarketBidOrder(tradable: Tradable): MarketBidOrder = {
    MarketBidOrder(randomIssuer(), randomQuantity(quantityDistribution), timestamp(), tradable, randomUUID())
  }

}


/** Companion object for RandomOrderGenerator.
  *
  * Contains helper functions for generating random `Order` instances.
  */
object RandomOrderGenerator {

  /** Generates a random issuer UUID.
    *
    * @return a `UUID` identifying the `issuer` for an `Order`.
    */
  def randomIssuer(): UUID = randomUUID()

  /** Generates a random limit price.
    *
    * @param priceDistribution sampling distribution for price values.
    * @return a price.
    */
  def randomPrice(priceDistribution: distribution.RealDistribution): Long = priceDistribution.sample().toLong

  /** Generates a random quantity.
    *
    * @param quantityDistribution sampling distribution for quantity values.
    * @return a quantity.
    */
  def randomQuantity(quantityDistribution: distribution.IntegerDistribution): Long = quantityDistribution.sample().toLong

  /** Generates a random UUID.
    *
    * @return a `UUID`.
    */
  def randomUUID(): UUID = UUID.randomUUID()

  /** Generates a timestamp.
    *
    * @return the current system time in milliseconds.
    */
  def timestamp(): Long = System.currentTimeMillis()

}
