package markets.orders


trait AggressiveBidOrder extends BidOrder {

  def predicate: AskOrder => Boolean

}
