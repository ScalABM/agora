package markets.engines

import markets.orders.Order

import scala.collection.immutable.Queue

trait ContinuousMatchingSchedule extends MatchingSchedule {

  /** Find a match for the incoming order.
    *
    * @param incoming the order to be matched.
    * @return a collection of matches.
    * @note Depending on size of the incoming order and the state of the market when the order is
    *       received, a single incoming order may generate several matches.
    */
  def findMatch(incoming: Order): Option[Queue[Matching]]

}
