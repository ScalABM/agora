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

  override def crosses(order: BidOrder): Boolean = {
    quantity > order.quantity || super.crosses(order)
  }

  def split(residualQuantity: Long): (SplittableMarketAskOrder, SplittableMarketAskOrder) = {
    val filledQuantity = quantity - residualQuantity
    (copy(filledQuantity), copy(residualQuantity))
  }

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
