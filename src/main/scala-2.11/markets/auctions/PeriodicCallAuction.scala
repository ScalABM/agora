package markets.auctions

import markets.auctions.orderbooks.PriorityOrderBook
import markets.orders.{AskOrder, BidOrder, Order}
import markets.tradables.Tradable
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.{AllowedSolution, BracketingNthOrderBrentSolver}

import scala.annotation.tailrec
import scala.collection.immutable.Queue


/** Class representing a call auction mechanism.
  *
  * @param initialPrice
  * @param tradable
  * @param relativeAccuracy
  * @param absoluteAccuracy
  * @param functionValueAccuracy
  * @param maximalOrder
  * @param maxEval
  */
class PeriodicCallAuction(initialPrice: Long,
                          tradable: Tradable,
                          relativeAccuracy: Double = 1e-9,
                          absoluteAccuracy: Double = 1e-6,
                          functionValueAccuracy: Double = 1e-15,
                          maximalOrder: Int = 5,
                          maxEval: Int = 500)
                         (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder])
  extends TwoSidedAuction {

  val askOrderBook = PriorityOrderBook[AskOrder](tradable)(askOrdering)

  val bidOrderBook = PriorityOrderBook[BidOrder](tradable)(bidOrdering)

  /** Fill orders
    *
    * @return some filled orders if possible; else none.
    */
  def fill(): Option[Queue[Matching]] = {
    currentPrice = findMarketClearingPrice(maxEval).toLong  // SIDE EFFECT!
    val filledOrders = accumulate(Queue.empty[Matching])
    if (filledOrders.nonEmpty) Some(filledOrders) else None
  }

  /** Rule specifying the transaction price between two orders.
    *
    * @param incoming the incoming order.
    * @param existing the order that resides at the top of the opposite order book.
    * @return the price at which a trade between the two orders will take place.
    */
  def formPrice(incoming: Order, existing: Order): Long = currentPrice

  /** Compute a market clearing price.
    *
    * @return price that equates aggregate demand with aggregate supply.
    * @note Possible that the algorithm will return a price such that excess demand will be
    *       negative (i.e., equilibrium price with excess supply). Method is protected at the
    *       package level for testing purposes.
    */
  protected[auctions] def findMarketClearingPrice(maxEval: Int): Double = {
    // try to be smart about the initial bracketing interval in order to speed convergence!
    val initialExcessDemand = excessDemandFunction.value(currentPrice)
    val (min, max) = if (initialExcessDemand > 0) (0L, currentPrice) else (currentPrice, Long.MaxValue)
    solver.solve(maxEval, excessDemandFunction, min, max, AllowedSolution.LEFT_SIDE)
  }

  /** Total quantity demanded for the tradable at the current price.
    *
    * @param price the current price.
    * @return the total quantity from all `BidOrder` whose limit price is greater than or equal to
    *         the current `price`.
    * @note protected at the package level for testing purposes.
    */
  protected[auctions] def aggregateDemand(price: Double): Double = {
    bidOrderBook.filter(order => order.price >= price).map(order => order.quantity).sum
  }

  /** Total quantity supplied for the tradable at the current price.
    *
    * @param price the current price.
    * @return the total quantity from all `AskOrder` whose limit price is less than or equal to
    *         the current `price`.
    * @note protected at the package level for testing purposes.
    */
  protected[auctions] def aggregateSupply(price: Double): Double = {
    askOrderBook.filter(order => order.price <= price).map(order => order.quantity).sum
  }

  /** Excess demand function.
    *
    * @note The excess demand function takes a price as its input and returns the difference
    *       between the total quantity demanded at that price (i.e., aggregate demand) and
    *       the total quantity supplied at that price (i.e., aggregate supply). The method
    *       is protected at the package level for testing purposes. */
  protected[auctions] val excessDemandFunction: UnivariateFunction = new UnivariateFunction {
    def value(price: Double): Double = aggregateDemand(price) - aggregateSupply(price)
  }

  @tailrec
  private[this] def accumulate(filledOrders: Queue[Matching]): Queue[Matching] = {
    askOrderBook.poll() match {
      case None => filledOrders
      case Some(order) => fill(order) match {
        case None => askOrderBook.add(order); filledOrders
        case Some(moreFilledOrders) => accumulate(filledOrders ++ moreFilledOrders)
      }
    }
  }

  private[this] var currentPrice = initialPrice

  private[this] val solver = {
    new BracketingNthOrderBrentSolver(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, maximalOrder)
  }

}


object PeriodicCallAuction {

  def apply(initialPrice: Long,
            tradable: Tradable,
            relativeAccuracy: Double = 1e-9,
            absoluteAccuracy: Double = 1e-6,
            functionValueAccuracy: Double = 1e-15,
            maximalOrder: Int = 5,
            maxEval: Int = 500)
           (implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder]): PeriodicCallAuction = {
    new PeriodicCallAuction(initialPrice, tradable, relativeAccuracy, absoluteAccuracy,
      functionValueAccuracy, maximalOrder)(askOrdering, bidOrdering)
  }

}
