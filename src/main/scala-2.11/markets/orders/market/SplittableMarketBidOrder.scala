package markets.orders.market

import akka.actor.ActorRef

import java.util.UUID

import markets.orders.{AskOrder, Splittable}
import markets.tradables.Tradable


class SplittableMarketBidOrder(issuer: ActorRef,
                               quantity: Long,
                               timestamp: Long,
                               tradable: Tradable,
                               uuid: UUID)
  extends MarketBidOrder(issuer, quantity, timestamp, tradable, uuid)
  with Splittable[MarketBidOrder] {

  /** Determines whether a SplittableMarketBidOrder crosses with some AskOrder.
    *
    * @param order some AskOrder
    * @return true if the SplittableMarketBidOrder crosses with the AskOrder; false otherwise.
    * @note A SplittableMarketBidOrder crosses with any...
    *       1. splittable AskOrder with weakly larger quantity.
    *       2. any AskOrder with weakly smaller quantity;
    */
  override def crosses(order: AskOrder): Boolean = {
    (order.isSplittable && quantity <= order.quantity) || quantity >= order.quantity
  }

  def split(residualQuantity: Long): (SplittableMarketBidOrder, SplittableMarketBidOrder) = {
    val filledQuantity = quantity - residualQuantity
    (copy(filledQuantity), copy(residualQuantity))
  }

  private[this] def copy(newQuantity: Long) = {
    new SplittableMarketBidOrder(this.issuer, newQuantity, this.timestamp, this.tradable, this.uuid)
  }

}


object SplittableMarketBidOrder {

  def apply(issuer: ActorRef,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable,
            uuid: UUID): MarketBidOrder = {
    new SplittableMarketBidOrder(issuer, quantity, timestamp, tradable, uuid)
  }

}
