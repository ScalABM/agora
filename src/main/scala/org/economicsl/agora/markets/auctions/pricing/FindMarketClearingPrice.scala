package org.economicsl.agora.markets.auctions.pricing

import org.economicsl.agora.markets.auctions.BrentSolverConfig
import org.economicsl.agora.markets.auctions.mutable.orderbooks.{AskOrderBook, BidOrderBook}
import org.economicsl.agora.markets.tradables.Price
import org.economicsl.agora.markets.tradables.orders.ask.SupplyFunction
import org.economicsl.agora.markets.tradables.orders.bid.DemandFunction

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver


class FindMarketClearingPrice[AB <: AskOrderBook[SupplyFunction], BB <: BidOrderBook[DemandFunction]]
                             (config: BrentSolverConfig, initialValue: Double)
  extends UniformPricingRule[SupplyFunction, AB, DemandFunction, BB]{

  def apply(askOrderBook: AB, bidOrderBook: BB): Price = {
    val F = ExcessDemand(askOrderBook, bidOrderBook)
    currentValue = solver.solve(config.maxEvaluations, F, config.min, config.max, config.startValue, config.allowedSolution)
    Price(currentValue)
  }

  /** Computes the aggregate demand at the current price.
    *
    * @param bidOrderBook a `BidOrderBook` containing `DemandFunction` instances.
    * @param current the `Price` for which each individual demand should be be computed.
    * @return the total quantity of demand.
    * @note computed aggregate demand is an `O(n)` operation where `n` is the `size` of the `bidOrderBook`.
    */
  private[this] def aggregateDemand(bidOrderBook: BB, current: Price): Double = {
    bidOrderBook.foldLeft(0.0)((demand, order) => demand + order.demand(current))
  }

  /** Computes the aggregate supply at the current price.
    *
    * @param askOrderBook an `AskOrderBook` containing `SupplyFunction` instances.
    * @param current the `Price` for which each individual supply should be be computed.
    * @return the total quantity of supply.
    * @note computed aggregate supply is an `O(n)` operation where `n` is the `size` of the `askOrderBook`.
    */
  private[this] def aggregateSupply(askOrderBook: AB, current: Price): Double = {
    askOrderBook.foldLeft(0.0)((supply, order) => supply + order.supply(current))
  }

  private[this] val solver = {
    new BracketingNthOrderBrentSolver(config.relativeAccuracy, config.absoluteAccuracy, config.functionValueAccuracy, config.maximalOrder)
  }

  /** Caches the most recently computed value of the price which is used to provide a "hot start" to the solver. */
  @volatile private[this] var currentValue = initialValue

  /** Class defining an excess demand function the root of which is the market clearing price.
    *
    * @param askOrderBook an `AskOrderBook` containing `SupplyFunction` instances.
    * @param bidOrderBook a `BidOrderBook` containing `DemandFunction` instances.
    */
  private[this] case class ExcessDemand(askOrderBook: AB, bidOrderBook: BB) extends UnivariateFunction {

    def value(x: Double): Double = {
      aggregateDemand(bidOrderBook, Price(x)) - aggregateSupply(askOrderBook, Price(x))
    }

  }

}