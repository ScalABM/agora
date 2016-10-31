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

import org.economicsl.agora.markets.tradables.orders.ask.{AskOrder, LimitAskOrder, MarketAskOrder}
import org.economicsl.agora.markets.tradables.orders.bid.{BidOrder, LimitBidOrder, MarketBidOrder}
import org.economicsl.agora.markets.tradables.Tradable
import org.apache.commons.math3.{distribution, random}


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
  def nextAskOrder(marketOrderProbability: Double, nonPriceCriteria: Option[(BidOrder) => Boolean], tradable: Tradable): AskOrder = {
    if (prng.nextDouble() < marketOrderProbability) {
      nextLimitAskOrder(nonPriceCriteria, tradable)
    } else {
      nextMarketAskOrder(nonPriceCriteria, tradable)
    }
  }

  /** Generates a random `LimitAskOrder` for a particular `Tradable`.
    *
    * @param limit the limit price.
    * @param nonPriceCriteria optional, non-price criteria used to determine whether a `BidOrder` is acceptable.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def nextLimitAskOrder(limit: Long, nonPriceCriteria: Option[(BidOrder) => Boolean], tradable: Tradable): LimitAskOrder = {
    val (issuer, quantity, timestamp, uuid) = (nextIssuer(), nextQuantity(askQuantityDistribution), nextTimestamp(), nextUUID())
    nonPriceCriteria match {
      case Some(_) => LimitAskOrder(issuer, limit, nonPriceCriteria, quantity, timestamp, tradable, uuid)
      case None => LimitAskOrder(issuer, limit, quantity, timestamp, tradable, uuid)
    }
  }

  /** Generates a random `LimitAskOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitAskOrder`.
    */
  def nextLimitAskOrder(nonPriceCriteria: Option[(BidOrder) => Boolean], tradable: Tradable): LimitAskOrder = {
    val (limit, quantity) = (nextPrice(askPriceDistribution), nextQuantity(askQuantityDistribution))
    val (issuer, timestamp, uuid) = (nextIssuer(), nextTimestamp(), nextUUID())
    nonPriceCriteria match {
      case Some(_) => LimitAskOrder(issuer, limit, nonPriceCriteria, quantity, timestamp, tradable, uuid)
      case None => LimitAskOrder(issuer, limit, quantity, timestamp, tradable, uuid)
    }
  }

  /** Generates a random `LimitBidOrder` for a particular `Tradable`.
    *
    * @param limit the limit price.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def nextLimitBidOrder(limit: Long, nonPriceCriteria: Option[(AskOrder) => Boolean], tradable: Tradable): LimitBidOrder = {
    val (issuer, quantity, timestamp, uuid) = (nextIssuer(), nextQuantity(bidQuantityDistribution), nextTimestamp(), nextUUID())
    nonPriceCriteria match {
      case Some(_) => LimitBidOrder(issuer, limit, nonPriceCriteria, quantity, timestamp, tradable, uuid)
      case None => LimitBidOrder(issuer, limit, quantity, timestamp, tradable, uuid)
    }
  }

  /** Generates a random `LimitBidOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `LimitBidOrder`.
    */
  def nextLimitBidOrder(nonPriceCriteria: Option[(AskOrder) => Boolean], tradable: Tradable): LimitBidOrder = {
    val (limit, quantity) = (nextPrice(bidPriceDistribution), nextQuantity(bidQuantityDistribution))
    val (issuer, timestamp, uuid) = (nextIssuer(), nextTimestamp(), nextUUID())
    nonPriceCriteria match {
      case Some(_) => LimitBidOrder(issuer, limit, nonPriceCriteria, quantity, timestamp, tradable, uuid)
      case None => LimitBidOrder(issuer, limit, quantity, timestamp, tradable, uuid)
    }
  }

  /** Generates a either a random `LimitAskOrder` or a `LimitBidOrder` for a particular `Tradable`.
    *
    * @param askOrderProbability probability of generating a `LimitAskOrder`.
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of either a `LimitAskOrder` or `LimitBidOrder`, depending.
    */
  def nextLimitOrder(askOrderProbability: Double, tradable: Tradable): Either[LimitAskOrder, LimitBidOrder] = {
    if (prng.nextDouble() < askOrderProbability) {
      Left(nextLimitAskOrder(None, tradable))
    } else {
      Right(nextLimitBidOrder(None, tradable))
    }
  }

  /** Generates a random `MarketAskOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `MarketAskOrder`.
    */
  def nextMarketAskOrder(nonPriceCriteria: Option[(BidOrder) => Boolean], tradable: Tradable): MarketAskOrder = {
    val (issuer, quantity, timestamp, uuid) = (nextIssuer(), nextQuantity(askQuantityDistribution), nextTimestamp(), nextUUID())
    nonPriceCriteria match {
      case Some(_) => MarketAskOrder(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
      case None => MarketAskOrder(issuer, quantity, timestamp, tradable, uuid)
    }
  }

  /** Generates a random `MarketBidOrder` for a particular `Tradable`.
    *
    * @param tradable the particular `Tradable` for which the order should be generated.
    * @return an instance of a `MarketBidOrder`.
    */
  def nextMarketBidOrder(nonPriceCriteria: Option[(AskOrder) => Boolean], tradable: Tradable): MarketBidOrder = {
    val (issuer, quantity, timestamp, uuid) = (nextIssuer(), nextQuantity(bidQuantityDistribution), nextTimestamp(), nextUUID())
    nonPriceCriteria match {
      case Some(_) => MarketBidOrder(issuer, nonPriceCriteria, quantity, timestamp, tradable, uuid)
      case None => MarketBidOrder(issuer, quantity, timestamp, tradable, uuid)
    }
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

  /** Generates a random limit price.
    *
    * @param priceDistribution sampling distribution for price values.
    * @return a price.
    */
  def nextPrice(priceDistribution: distribution.RealDistribution): Long = priceDistribution.sample().toLong

  /** Generates a random quantity.
    *
    * @param quantityDistribution sampling distribution for quantity values.
    * @return a quantity.
    */
  def nextQuantity(quantityDistribution: distribution.IntegerDistribution): Long = quantityDistribution.sample().toLong

  /** Generates a random UUID.
    *
    * @return a `UUID`.
    */
  def nextUUID(): UUID = UUID.randomUUID()

  /** Generates a timestamp.
    *
    * @return the current system time in milliseconds.
    */
  def nextTimestamp(): Long = System.currentTimeMillis()

}
