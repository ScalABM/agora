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

import java.util.UUID

import org.economicsl.agora.markets.tradables.orders.ask.{AskOrder, LimitAskOrder, PersistentLimitAskOrder, PersistentMarketAskOrder}
import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder, PersistentLimitBidOrder, PersistentMarketBidOrder}
import org.economicsl.agora.markets.tradables.{Price, Quantity, Tradable}
import org.apache.commons.math3.{distribution, random}
import org.economicsl.agora.markets.tradables.orders.Persistent


/** Class for generating random `Order` instances for testing purposes.
  *
  * @param prng a pseudo-random number generator.
  * @param askPriceDistribution sampling distribution for `AskOrder` prices.
  * @param askQuantityDistribution sampling distribution for `AskOrder` quantities.
  * @param bidPriceDistribution sampling distribution for `BidOrder` prices.
  * @param bidQuantityDistribution sampling distribution for `BidOrder` quantities.
  */
class RandomOrderGenerator(val prng: random.RandomGenerator,
                           val askPriceDistribution: distribution.RealDistribution,
                           val askQuantityDistribution: distribution.IntegerDistribution,
                           val bidPriceDistribution: distribution.RealDistribution,
                           val bidQuantityDistribution: distribution.IntegerDistribution) {

  /* Make static methods defined on the companion object accessible. */
  import RandomOrderGenerator._

  /** Generates a random `AskOrder` for a particular `Tradable`.
    *
    * @param marketOrderProbability the probability of a `MarketAskOrder`. Default is 0.5.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def nextAskOrder(marketOrderProbability: Double, tradable: Tradable): LimitAskOrder with Persistent with Quantity = {
    if (prng.nextDouble() < marketOrderProbability) {
      nextLimitAskOrder(tradable)
    } else {
      nextMarketAskOrder(tradable)
    }
  }

  /** Generates a random `BidOrder` for a particular `Tradable`.
    *
    * @param marketOrderProbability the probability of a `MarketBidOrder`. Default is 0.5.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def nextBidOrder(marketOrderProbability: Double, tradable: Tradable): LimitBidOrder with Persistent with Quantity = {
    if (prng.nextDouble() < marketOrderProbability) {
      nextLimitBidOrder(tradable)
    } else {
      nextMarketBidOrder(tradable)
    }
  }

  /** Generates a random `LimitAskOrder` for a particular `Tradable`.
    *
    * @param limit the limit price.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def nextLimitAskOrder(limit: Price, tradable: Tradable): PersistentLimitAskOrder with Quantity = {
    val (issuer, quantity, uuid) = (nextIssuer(), nextQuantity(askQuantityDistribution), nextUUID())
    PersistentLimitAskOrder(issuer, limit, quantity, tradable, uuid)
  }

  /** Generates a random `LimitAskOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def nextLimitAskOrder(tradable: Tradable): PersistentLimitAskOrder with Quantity = {
    val (limit, quantity) = (nextPrice(askPriceDistribution), nextQuantity(askQuantityDistribution))
    val (issuer, uuid) = (nextIssuer(), nextUUID())
    PersistentLimitAskOrder(issuer, limit, quantity, tradable, uuid)
  }

  /** Generates a random `LimitBidOrder` for a particular `Tradable`.
    *
    * @param limit the limit price.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def nextLimitBidOrder(limit: Price, tradable: Tradable): PersistentLimitBidOrder with Quantity = {
    val (issuer, quantity, uuid) = (nextIssuer(), nextQuantity(bidQuantityDistribution), nextUUID())
    PersistentLimitBidOrder(issuer, limit, quantity, tradable, uuid)
  }

  /** Generates a random `LimitBidOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def nextLimitBidOrder(tradable: Tradable): PersistentLimitBidOrder with Quantity = {
    val (limit, quantity) = (nextPrice(bidPriceDistribution), nextQuantity(bidQuantityDistribution))
    val (issuer, uuid) = (nextIssuer(), nextUUID())
    PersistentLimitBidOrder(issuer, limit, quantity, tradable, uuid)
  }

  /** Generates a either a random `LimitAskOrder` or a `LimitBidOrder` for a particular `Tradable`.
    *
    * @param askOrderProbability probability of generating a `LimitAskOrder`.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of either a `LimitAskOrder` or `LimitBidOrder`, depending.
    */
  def nextLimitOrder(askOrderProbability: Double, tradable: Tradable): Either[PersistentLimitAskOrder with Quantity, PersistentLimitBidOrder with Quantity] = {
    if (prng.nextDouble() < askOrderProbability) {
      Left(nextLimitAskOrder(tradable))
    } else {
      Right(nextLimitBidOrder(tradable))
    }
  }

  /** Generates a random `MarketAskOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `MarketAskOrder`.
    */
  def nextMarketAskOrder(tradable: Tradable): PersistentMarketAskOrder with Quantity = {
    val (issuer, quantity, uuid) = (nextIssuer(), nextQuantity(askQuantityDistribution), nextUUID())
    PersistentMarketAskOrder(issuer, quantity, tradable, uuid)
  }

  /** Generates a random `MarketBidOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `MarketBidOrder`.
    */
  def nextMarketBidOrder(tradable: Tradable): PersistentMarketBidOrder with Quantity = {
    val (issuer, quantity, uuid) = (nextIssuer(), nextQuantity(bidQuantityDistribution), nextUUID())
    PersistentMarketBidOrder(issuer, quantity, tradable, uuid)
  }

}


/** Companion object for RandomOrderGenerator.
  *
  * Contains helper functions for generating random `Order` instances.
  */
object RandomOrderGenerator {

  /** Create an instance of a `RandomOrderGenerator`.
    *
    * @param prng a pseudo-random number generator.
    * @param askPriceDistribution sampling distribution for `AskOrder` prices.
    * @param askQuantityDistribution sampling distribution for `AskOrder` quantities.
    * @param bidPriceDistribution sampling distribution for `BidOrder` prices.
    * @param bidQuantityDistribution sampling distribution for `BidOrder` quantities.
    */
  def apply(prng: random.RandomGenerator, askPriceDistribution: distribution.RealDistribution,
            askQuantityDistribution: distribution.IntegerDistribution, bidPriceDistribution: distribution.RealDistribution,
            bidQuantityDistribution: distribution.IntegerDistribution): RandomOrderGenerator = {
    new RandomOrderGenerator(prng, askPriceDistribution, askQuantityDistribution, bidPriceDistribution, bidQuantityDistribution)
  }

  /** Create an instance of a `RandomOrderGenerator`.
    *
    * @param prng a pseudo-random number generator.
    * @param priceDistribution sampling distribution for prices.
    * @param quantityDistribution sampling distribution for quantities.
    */
  def apply(prng: random.RandomGenerator,
            priceDistribution: distribution.RealDistribution,
            quantityDistribution: distribution.IntegerDistribution): RandomOrderGenerator = {
    new RandomOrderGenerator(prng, priceDistribution, quantityDistribution, priceDistribution, quantityDistribution)
  }

  /** Generates a random issuer UUID.
    *
    * @return a `UUID` identifying the `issuer` for an `Order`.
    */
  def nextIssuer(): UUID = nextUUID()

  /** Generates a random UUID.
    *
    * @return a `UUID`.
    */
  def nextUUID(): UUID = UUID.randomUUID()

  /** Generates a random limit price.
    *
    * @param valueDistribution sampling distribution for the underlying `Price` value.
    * @return a `Price` instance.
    */
  def nextPrice(valueDistribution: distribution.RealDistribution): Price = Price(valueDistribution.sample())

  /** Generates a random quantity.
    *
    * @param quantityDistribution sampling distribution for quantity values.
    * @return a quantity.
    */
  def nextQuantity(quantityDistribution: distribution.IntegerDistribution): Long = quantityDistribution.sample().toLong

}
