package org.economicsl.agora.markets.auctions

import org.economicsl.agora.markets.tradables.orders.{Order, Persistent}


trait DominanceRule[O <: Order with Persistent] {
  this: AuctionLike[O] =>
}


/** Requires that new orders be superior to existing orders. */
trait AscendingRule[O <: Order with Persistent] extends DominanceRule[O] {
  this: AuctionLike[O] =>

  def place(order: O): Either[Reject, Accept] = orderBook.get(order.issuer) match {
    case Some(existingOrder) if order.isSuperior(existingOrder) => orderBook.add(order); Right(Accept)
    case _ => Left(Reject)
  }

}


/** Requires that new orders be inferior to existing orders. */
trait DescendingRule[O <: Order with Persistent] extends DominanceRule[O] {
  this: AuctionLike[O] =>

  def place(order: O): Either[Reject, Accept] = orderBook.get(order.issuer) match {
    case Some(existingOrder) if order.isInferior(existingOrder) => orderBook.add(order); Right(Accept)
    case _ => Left(Reject)
  }

}


trait BeatTheQuoteRule[O <: Order with Persistent] {
  this: AuctionLike[O] =>

}
