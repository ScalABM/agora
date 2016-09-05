package markets.orders


trait ExhaustiveAskOrder extends AskOrder {

  /** A boolean function that defines the set of acceptable `BidOrder` instances.
    *
    * @return a boolean function.
    * @note a `MatchingEngine` will use this `predicate` to `filter` its `bidOrderBook`.
    */
  def predicate: BidOrder => Boolean

  /** A binary operator used to select a single `BidOrder` from a collection of `BidOrder` instances.
    *
    * @return a binary operator.
    * @note a `MatchingEngine` will uses this `operator` to `reduce` the filtered `bidOrderBook`.
    */
  def operator: (BidOrder, BidOrder) => BidOrder

}