package markets.auctions

import markets.auctions.orderbooks.PriorityOrderBook
import markets.orders.{AskOrder, BidOrder, Order}
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
class CallAuction(initialPrice: Double,
                  tradable: Tradable,
                  relativeAccuracy: Double = 1e-9,
                  absoluteAccuracy: Double = 1e-6,
                  functionValueAccuracy: Double = 1e-15,
                  maximalOrder: Int = 5,
                  maxEval: Int = 500)
                 (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder])
  extends TwoSidedAuctionMechanism {

  val askOrderBook = PriorityOrderBook[AskOrder](tradable)(askOrdering)

  val bidOrderBook = PriorityOrderBook[BidOrder](tradable)(bidOrdering)

  def fill(): Iterable[Matching] = {
    currentPrice = findMarketClearingPrice(maxEval)  // SIDE EFFECT!

    // ration quantities
    ???

    // cancel any remaining orders?
    ???
  }


  /** Rule specifying the transaction price between two orders.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite order book.
    * @return the price at which a trade between the two orders will take place.
    */
  def formPrice(incoming: Order, existing: Order): Double = currentPrice

  /** Compute the market clearing price.
    *
    * @return the price that equates aggregate demand with aggregate supply.
    * @note Possible that the algorithm will return a price such that excess demand will be
    *       negative (i.e., equilibrium price with excess supply). Method is protected at the
    *       package level for testing purposes.
    */
  protected[auctions] def findMarketClearingPrice(maxEval: Int): Double = {
    // try to be smart about the initial bracketing interval in order to speed convergence!
    val initialExcessDemand = excessDemand.value(currentPrice)
    val (min, max) = if (initialExcessDemand > 0) (0, currentPrice) else (currentPrice, Long.MaxValue)
    solver.solve(maxEval, excessDemand, min, max, AllowedSolution.LEFT_SIDE)
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

  private[this] var currentPrice = initialPrice

  private[this] val solver = {
    new BracketingNthOrderBrentSolver(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, maximalOrder)
  }

}
