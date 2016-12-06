package org.economicsl.agora.markets.auctions.pricing

import org.economicsl.agora.markets.auctions.mutable.orderbooks.OrderBook
import org.economicsl.agora.markets.tradables.Price
import org.apache.commons.math3.analysis.MultivariateVectorFunction
import org.apache.commons.math3.fitting.leastsquares
import org.economicsl.agora.markets.tradables.orders.MultivariateExcessDemandFunction


class MultivariateMarketClearingPrice(initialValue: Vector[Price]) extends (OrderBook[MultivariateExcessDemandFunction] => Vector[Price]) {

  def apply(orderBook: OrderBook[MultivariateExcessDemandFunction]): Vector[Price] = {
    optimum = optimizer.optimize(leastSquaresProblem(orderBook))
    optimum.getPoint  // todo need to do some kind of type conversion??
  }

  private[this] val optimizer = new leastsquares.LevenbergMarquardtOptimizer()

  /** Caches the most recently computed value of the price vector which is used to provide a "hot start" to the solver. */
  @volatile private[this] var optimum: leastsquares.LeastSquaresOptimizer.Optimum = {
    // need to construct an instance of Optimum using information from the config object and some initial price vector
    ???
  }

  /** Return a new `LeastSquaresProblem` given an `OrderBook` containing individual excess demand functions.
    *
    * Defining the least-squares problem involves defining a target (in this vase an array of zeros!) and a model (in
    * this case an aggregate excess demand function constructed from the order book containing individual excess demand
    * functions
    *
    * See Apache docs for details (http://commons.apache.org/proper/commons-math/userguide/optimization.html)
    */
  private[this] def leastSquaresProblem(orderBook: OrderBook[MultivariateExcessDemandFunction]): leastsquares.LeastSquaresProblem = {
    val equilibrium = Array.fill(numberTradables)(0.0)
    ???
  }

  /** Class defining an excess demand function which is the "model" provided to the least-squares problem. */
  private[this] case class AggregateExcessDemand(orderBook: OrderBook[MultivariateExcessDemandFunction])
    extends MultivariateVectorFunction {

    def value(prices: Array[Double]): Array[Double] = {
      val numberTradables = initialValue.length
      val initialExcessDemand = Array.fill[Long](numberTradables)(0)
      orderBook.foldLeft(initialExcessDemand)((total, f) => total.zip(f.excessDemand(prices)).map { case (x, y) => x + y })
    }

  }

}