package markets.orders.market

import akka.actor.ActorRef

import java.util.UUID

import markets.orders.{BidOrder, Splittable}
import markets.tradables.Tradable


class SplittableMarketAskOrder(issuer: ActorRef,
                               quantity: Long,
                               timestamp: Long,
                               tradable: Tradable,
                               uuid: UUID)
  extends MarketAskOrder(issuer, quantity, timestamp, tradable, uuid)
  with Splittable[MarketAskOrder] {

  /** Determines whether a SplittableMarketAskOrder crosses with some BidOrder.
    *
    * @param order
    * @return
    * @note A SplittableMarketAskOrder crosses with any...
    *       1. splittable BidOrder with weakly larger quantity.
    *       2. any BidOrder with weakly smaller quantity;
    */
  override def crosses(order: BidOrder): Boolean = {
    (order.isSplittable && quantity <= order.quantity) || quantity >= order.quantity
  }

  def split(residualQuantity: Long): (SplittableMarketAskOrder, SplittableMarketAskOrder) = {
    (copy(quantity - residualQuantity), copy(residualQuantity))
  }

  /* Creates a copy of a splittable market ask order with a new quantity. */
  private[this] def copy(newQuantity: Long) = {
    new SplittableMarketAskOrder(this.issuer, newQuantity, this.timestamp, this.tradable, this.uuid)
  }

}


object SplittableMarketAskOrder {

  def apply(issuer: ActorRef,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable,
            uuid: UUID): MarketAskOrder = {
    new SplittableMarketAskOrder(issuer, quantity, timestamp, tradable, uuid)
  }

}
