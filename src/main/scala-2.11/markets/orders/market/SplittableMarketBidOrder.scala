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

  override def crosses(order: AskOrder): Boolean = {
    quantity > order.quantity || super.crosses(order)
  }

  def split(residualQuantity: Long): (SplittableMarketBidOrder, SplittableMarketBidOrder) = {
    val filledQuantity = quantity - residualQuantity
    (copy(filledQuantity), copy(residualQuantity))
  }

  private[this] def copy(newQuantity: Long) = {
    new SplittableMarketBidOrder(this.issuer, newQuantity, this.timestamp, this.tradable, this.uuid)
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
