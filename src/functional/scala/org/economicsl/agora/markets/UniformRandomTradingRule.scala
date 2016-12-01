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
package org.economicsl.agora.markets

import java.util.UUID

import org.economicsl.agora.markets.tradables.{Price, SingleUnit, Tradable}
import org.economicsl.agora.markets.tradables.orders.ask.{LimitAskOrder, PersistentLimitAskOrder}
import org.economicsl.agora.markets.tradables.orders.bid.{LimitBidOrder, PersistentLimitBidOrder}
import org.apache.commons.math3.distribution
import org.apache.commons.math3.random.RandomGenerator


class UniformRandomTradingRule(askOrderProbability: Double, issuer: UUID, prng: RandomGenerator)
  extends TradingRule[LimitAskOrder with SingleUnit, LimitBidOrder with SingleUnit] {

  def apply(tradable: Tradable): Either[LimitAskOrder with SingleUnit, LimitBidOrder with SingleUnit] = {
    if (prng.nextDouble() <= askOrderProbability) {
      val limit = Price(askPriceDistribution.sample())
      Left(PersistentLimitAskOrder(issuer, limit, tradable, UUID.randomUUID()))
    } else {
      val limit = Price(bidPriceDistribution.sample())
      Right(PersistentLimitBidOrder(issuer, limit, tradable, UUID.randomUUID()))
    }
  }

  private[this] val askPriceDistribution = {
    new distribution.UniformRealDistribution(prng, 50, 200)
  }

  private[this] val bidPriceDistribution = {
    new distribution.UniformRealDistribution(prng, 1, 150)
  }

}
