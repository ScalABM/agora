package markets.auctions

import markets.orderbooks.AbstractOrderBook
import markets.orders.{AskOrder, BidOrder}


trait SellerPostedPriceAuction {

  def cancel(order: AskOrder): Option[AskOrder] = askOrderBook.remove(order.uuid)

  def fill(order: BidOrder): Option[Iterable[Matching]]

  def place(order: AskOrder): Unit = askOrderBook.add(order)

  protected[auctions] def askOrderBook: AbstractOrderBook[AskOrder]

}
