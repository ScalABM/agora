package markets.engines
import markets.orderbooks.mutable.PriorityOrderBook
import markets.orders.{AskOrder, BidOrder}
import markets.tradables.Tradable


class PriorityMatchingEngine(tradable: Tradable)(implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder])
  extends AbstractMatchingEngine {

  val askOrderBook = PriorityOrderBook(tradable)(askOrdering)

  val bidOrderBook = PriorityOrderBook(tradable)(bidOrdering)

  /** Partial function defining the logic for matching an `AskOrder` with a `BidOrder`.
    *
    * @note default logic neither adds an unmatched `AskOrder` to the `askOrderBook`, nor removes a matched `BidOrder`
    *       from the `bidOrderBook` as the desired timing of `add` (`remove`) operations can depend on higher level
    *       implementation details.
    */
  override val matchWithBidOrder: PartialFunction[AskOrder, Option[BidOrder]] = {
    case order: AskOrder => bidOrderBook.headOption match {
      case Some(bidOrder) => if (order.predicate(bidOrder)) bidOrderBook.remove() else { askOrderBook.add(order); None }
      case None => askOrderBook.add(order); None
    }
  }

  /** Partial function defining the logic for matching a `BidOrder` with an `AskOrder`.
    *
    * @note default logic neither adds an unmatched `BidOrder` to the `bidOrderBook`, nor removes a matched `AskOrder`
    *       from the `askOrderBook` as the desired timing of `add` (`remove`) operations can depend on higher level
    *       implementation details.
    */
  override val matchWithAskOrder: PartialFunction[BidOrder, Option[AskOrder]] = {
    case order: BidOrder => askOrderBook.headOption match {
      case Some(askOrder) => if (order.predicate(askOrder)) askOrderBook.remove() else { bidOrderBook.add(order); None }
      case None => bidOrderBook.add(order); None
    }
  }

}


object PriorityMatchingEngine {

  def apply(tradable: Tradable)(implicit askOrdering: Ordering[AskOrder], bidOrdering: Ordering[BidOrder]): PriorityMatchingEngine = {
    new PriorityMatchingEngine(tradable)(askOrdering, bidOrdering)
  }

}
