package markets.auctions

import markets.orderbooks.AbstractOrderBook
import markets.orders.{AskOrder, BidOrder}


trait BuyerPostedPriceAuction {

  def cancel(order: BidOrder): Option[BidOrder] = bidOrderBook.remove(order.uuid)

  def fill(order: AskOrder): Option[Iterable[Matching]]

  def place(order: BidOrder): Unit = bidOrderBook.add(order)

  protected[auctions] def bidOrderBook: AbstractOrderBook[BidOrder]

}
