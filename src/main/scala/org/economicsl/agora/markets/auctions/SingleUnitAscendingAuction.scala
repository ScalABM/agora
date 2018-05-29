package org.economicsl.agora.markets.auctions

import java.util.UUID

import org.economicsl.agora.markets.Fill
import org.economicsl.agora.markets.auctions.orderbooks.SortedBidOrderBook
import org.economicsl.agora.markets.tradables.{Price, SingleUnit, Tradable}
import org.economicsl.agora.markets.tradables.orders.Persistent
import org.economicsl.agora.markets.tradables.orders.ask.LimitAskOrder
import org.economicsl.agora.markets.tradables.orders.bid.LimitBidOrder


trait SingleUnitAscendingAuction[A <: LimitAskOrder with SingleUnit, B <: LimitBidOrder with Persistent with SingleUnit]
  extends SingleUnitAuction[A, B] with AscendingBidOrders[B] {

  type OB = SortedBidOrderBook[B]

  final def fill(order: A): Option[Fill] = findMatchFor(order, orderBook) map {
    case (_, bidOrder) =>
      orderBook = orderBook - (bidOrder.issuer, bidOrder) // SIDE EFFECT!
      val price = formPrice(order, bidOrder)
      new Fill(order, bidOrder, price, 1)
  }

  final def place(order: B): Unit = orderBook = orderBook + (order.issuer, order)

  protected def formPrice(askOrder: A, bidOrder: B): Price

  protected def findMatchFor(order: A, orderBook: SortedBidOrderBook[B]): Option[(UUID, B)]

  @volatile protected var orderBook: OB

}


object SingleUnitAscendingAuction {

  def apply[A <: LimitAskOrder with SingleUnit, B <: LimitBidOrder with Persistent with SingleUnit]
           (m: (A, SortedBidOrderBook[B]) => Option[(UUID, B)], p: (A, B) => Price, tradable: Tradable)
           (implicit ordering: Ordering[(UUID, B)])
           : SingleUnitAscendingAuction[A, B] = {

    new SingleUnitAscendingAuction[A, B] {

      val tradable = tradable
      protected def formPrice(askOrder: A, bidOrder: B): Price = p(askOrder, bidOrder)

      protected def findMatchFor(order: A, orderBook: SortedBidOrderBook[B]): Option[(UUID, B)] = m(order, orderBook)

      protected var orderBook: SortedBidOrderBook[B] =

    }

  }


  private[this] class DefaultImpl[A <: LimitAskOrder with SingleUnit, B <: LimitBidOrder with Persistent with SingleUnit]
                () extends SingleUnitAscendingAuction {




  }

}