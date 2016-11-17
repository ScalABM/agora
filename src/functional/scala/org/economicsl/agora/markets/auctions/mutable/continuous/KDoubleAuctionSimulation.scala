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
package org.economicsl.agora.markets.auctions.mutable.continuous

import org.apache.commons.math3.distribution
import org.economicsl.agora.markets.tradables.{LimitPrice, Tradable}
import org.economicsl.agora.markets.tradables.orders.Order


object KDoubleAuctionSimulation {

  /** Abstract base class for trading rules as defined in Satterthwaite and Williams (JET, 1989).
    *
    * @param buyerValuations the distribution of buyer valuations.
    * @param sellerValuations the distribution of seller valuations.
    * @tparam O a type of `Order with LimitPrice`.
    * @note both the buyer and seller valuation distributions should be defined on the unit interval.
    */
  protected[auctions] abstract class TradingRule[+O <: Order with LimitPrice](buyerValuations: distribution.RealDistribution,
                                                                              sellerValuations: distribution.RealDistribution)
    extends ((Tradable) => O) {

    /* All theoretical results in the paper were derived for valuation distributions defined on [0, 1]! */
    require(buyerValuations.getSupportLowerBound == 0.0 && buyerValuations.getSupportUpperBound == 1.0)
    require(sellerValuations.getSupportLowerBound == 0.0 && sellerValuations.getSupportUpperBound == 1.0)

    protected def buyerInverseHazardRate(reservationValue: Double): Double = {
      (buyerValuations.cumulativeProbability(reservationValue) - 1) / buyerValuations.density(reservationValue)
    }

    protected def sellerInverseHazardRate(reservationValue: Double): Double = {
      sellerValuations.cumulativeProbability(reservationValue) / sellerValuations.density(reservationValue)
    }

    /** Virtual reservation values (terminology due to Myerson (Econometrica, 1984). */
    protected def virtualReservationValue(inverseHazardRate: (Double) => Double, reservationValue: Double): Double = {
      reservationValue + inverseHazardRate(reservationValue)
    }

  }

}
