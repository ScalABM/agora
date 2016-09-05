package markets.orders


trait ExhaustiveBidOrder extends BidOrder {

  /** A boolean function that defines the set of acceptable `AskOrder` instances.
    *
    * @return a boolean function.
    * @note a `MatchingEngine` will use this `predicate` to `filter` its `askOrderBook`.
    */
  def predicate: AskOrder => Boolean

  /** A binary operator used to select a single `AskOrder` from a collection of `AskOrder` instances.
    *
    * @return a binary operator.
    * @note a `MatchingEngine` will uses this `operator` to `reduce` the filtered `askOrderBook`.
    */
  def operator: (AskOrder, AskOrder) => AskOrder

}
