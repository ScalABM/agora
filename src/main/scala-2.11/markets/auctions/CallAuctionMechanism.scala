package markets.auctions

import markets.auctions.orderbooks.OrderBook
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.{AllowedSolution, BracketingNthOrderBrentSolver}


/** Class representing a call auction mechanism.
  *
  * @param tradable
  * @param initialPrice
  * @param relativeAccuracy
  * @param absoluteAccuracy
  * @param functionValueAccuracy
  * @param maximalOrder
  * @param maxEval
  */
class CallAuctionMechanism(tradable: Tradable,
                           initialPrice: Double,
                           relativeAccuracy: Double = 1e-9,
                           absoluteAccuracy: Double = 1e-6,
                           functionValueAccuracy: Double = 1e-15,
                           maximalOrder: Int = 5,
                           maxEval: Int = 500) {

  val askOrderBook = OrderBook[AskOrder](tradable)

  val bidOrderBook = OrderBook[BidOrder](tradable)

  def fill(): Double = {
    val initialExcessDemand = excessDemand.value(mostRecentPrice)
    val (min, max) = if (initialExcessDemand > 0) (0, mostRecentPrice) else (mostRecentPrice, Long.MaxValue)
    mostRecentPrice = solver.solve(maxEval, excessDemand, min, max, AllowedSolution.LEFT_SIDE)
    mostRecentPrice
  }

  /** Total quantity demanded for the tradable at the current price.
    *
    * @param price the current price.
    * @return the total quantity from all `BidOrder` whose limit price is greater than or equal to
    *         the current `price`.
    * @note protected at the package level for testing purposes.
    */
  protected[auctions] def aggregateDemand(price: Double): Long = {
    bidOrderBook.filter(order => order.price >= price).map(order => order.quantity).sum
  }

  /** Total quantity supplied for the tradable at the current price.
    *
    * @param price the current price.
    * @return the total quantity from all `AskOrder` whose limit price is less than or equal to
    *         the current `price`.
    * @note protected at the package level for testing purposes.
    */
  protected[auctions] def aggregateSupply(price: Double): Long = {
    askOrderBook.filter(order => order.price <= price).map(order => order.quantity).sum
  }

  /* protected at the package level for testing purposes. */
  protected[auctions] val excessDemand = new UnivariateFunction {
    def value(price: Double): Double = aggregateDemand(price) - aggregateSupply(price)
  }

  private[this] var mostRecentPrice = initialPrice

  private[this] val solver = {
    new BracketingNthOrderBrentSolver(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, maximalOrder)
  }

}
