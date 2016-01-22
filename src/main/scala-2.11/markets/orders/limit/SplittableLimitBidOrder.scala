package markets.orders.limit

import akka.actor.ActorRef

import java.util.UUID

import markets.orders.market.MarketAskOrder
import markets.orders.{AskOrder, Splittable}
import markets.tradables.Tradable


class SplittableLimitBidOrder(issuer: ActorRef,
                              price: Long,
                              quantity: Long,
                              timestamp: Long,
                              tradable: Tradable,
                              uuid: UUID)
  extends LimitBidOrder(issuer, price, quantity, timestamp, tradable, uuid)
    with Splittable[LimitBidOrder] {

  /** Determines whether or not a SplittableLimitBidOrder crosses some AskOrder.
    *
    * @param order some AskOrder.
    * @return true if the SplittableBidOrder crosses with the AskOrder; false otherwise.
    * @note A SplittableLimitBidOrder should cross with any...
    *       1. SplittableMarketAskOrder;
    *       2. MarketAskOrder with weakly smaller quantity;
    *       3. SplittableLimitAskOrder with a strictly lower limit price;
    *       4. LimitAskOrder with a strictly lower limit price and weakly smaller quantity.
    */
  override def crosses(order: AskOrder): Boolean = order match {
    case _: MarketAskOrder =>
      order.isSplittable || quantity >= order.quantity
    case _: LimitAskOrder if price > order.price =>
      order.isSplittable || quantity >= order.quantity
    case _ => false
  }

  def split(residualQuantity: Long): (SplittableLimitBidOrder, SplittableLimitBidOrder) = {
    (copy(quantity - residualQuantity), copy(residualQuantity))
  }

  /* Creates a copy of a splittable LimitBidOrder with a new quantity. */
  private[this] def copy(newQuantity: Long) = {
    new SplittableLimitBidOrder(issuer, price, newQuantity, timestamp, tradable, uuid)
  }

}


object SplittableLimitBidOrder {

  def apply(issuer: ActorRef,
            price: Long,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable,
            uuid: UUID): SplittableLimitBidOrder = {
    new SplittableLimitBidOrder(issuer, price, quantity, timestamp, tradable, uuid)
  }

}
