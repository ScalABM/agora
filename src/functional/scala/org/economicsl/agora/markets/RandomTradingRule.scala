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

import org.economicsl.agora.markets.tradables.{LimitPrice, Price, SingleUnit, Tradable}
import org.economicsl.agora.markets.tradables.orders.ask.PersistentLimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.PersistentLimitBidOrder
import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}

import org.apache.commons.math3.distribution


sealed abstract class RandomTradingRule[+O <: Order with LimitPrice with Persistent with SingleUnit]
                                       (val issuer: UUID, values: distribution.RealDistribution)
  extends TradingRule[O] {

  def observe: PartialFunction[Any, Unit] = {
    case _ => ??? // do nothing?
  }

  /* Choose the `limit` price for the order by sampling from the underlying distribution. */
  protected def limit(): Price = Price(values.sample())

}


class RandomBuyer(issuer: UUID, values: distribution.RealDistribution)
  extends RandomTradingRule[PersistentLimitBidOrder with SingleUnit](issuer, values) {

  def apply(tradable: Tradable): PersistentLimitBidOrder with SingleUnit = {
    PersistentLimitBidOrder(issuer, limit(), tradable, UUID.randomUUID())
  }

}


class RandomSeller(issuer: UUID, values: distribution.RealDistribution)
  extends RandomTradingRule[PersistentLimitAskOrder with SingleUnit](issuer, values) {

  def apply(tradable: Tradable): PersistentLimitAskOrder with SingleUnit = {
    PersistentLimitAskOrder(issuer, limit(), tradable, UUID.randomUUID())
  }

}
