package markets.orders


trait AggressiveAskOrder extends AskOrder {

  def predicate: BidOrder => Boolean

}
