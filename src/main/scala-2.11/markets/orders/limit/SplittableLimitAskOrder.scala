package markets.orders.limit

import akka.actor.ActorRef

import java.util.UUID

import markets.orders.market.MarketBidOrder
import markets.orders.{BidOrder, Splittable}
import markets.tradables.Tradable


class SplittableLimitAskOrder(issuer: ActorRef,
                              price: Long,
                              quantity: Long,
                              timestamp: Long,
                              tradable: Tradable,
                              uuid: UUID)
  extends LimitAskOrder(issuer, price, quantity, timestamp, tradable, uuid)
    with Splittable[LimitAskOrder] {

  /** Determines whether or not a SplittableLimitAskOrder crosses some BidOrder.
    *
    * @param order some BidOrder.
    * @return true if the SplittableAskOrder crosses with the BidOrder; false otherwise.
    * @note A SplittableLimitAskOrder should cross with...
    *       1. any SplittableMarketBidOrder;
    *       2. any MarketBidOrder with weakly smaller quantity;
    *       3. any SplittableLimitBidOrder with a strictly higher limit price;
    *       4. any LimitBidOrder with a strictly higher limit price and weakly smaller quantity.
    */
  override def crosses(order: BidOrder): Boolean = order match {
    case _: MarketBidOrder =>
      order.isSplittable || quantity >= order.quantity
    case _: LimitBidOrder if price < order.price =>
      order.isSplittable || quantity >= order.quantity
    case _ => false
  }

  def split(residualQuantity: Long): (SplittableLimitAskOrder, SplittableLimitAskOrder) = {
    (copy(quantity - residualQuantity), copy(residualQuantity))
  }

  /* Creates a copy of a SplittableLimitAskOrder with a new quantity. */
  private[this] def copy(newQuantity: Long) = {
    new SplittableLimitAskOrder(issuer, price, newQuantity, timestamp, tradable, uuid)
  }

}


object SplittableLimitAskOrder {

  def apply(issuer: ActorRef,
            price: Long,
            quantity: Long,
            timestamp: Long,
            tradable: Tradable,
            uuid: UUID): SplittableLimitAskOrder = {
    new SplittableLimitAskOrder(issuer, price, quantity, timestamp, tradable, uuid)
  }

}
