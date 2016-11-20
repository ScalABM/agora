package org.economicsl.agora.markets.tradables.orders.ask

import java.util.UUID

import org.economicsl.agora.markets.tradables.{Price, Tradable}
import org.economicsl.agora.markets.tradables.orders.{AllOrNone, Persistent}
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder


/** Trait defining an order to sell a `Tradable` that must be executed in its entirety, or not executed at all. */
trait AONLimitAskOrder extends LimitAskOrder with Persistent with AllOrNone[BidOrder with Persistent] {
  
  override def isAcceptable: (BidOrder with Persistent) => Boolean = nonPriceCriteria match {
    case Some(additionalCriteria) => order => super.isAcceptable(order) && additionalCriteria(order)
    case None => super.isAcceptable
  }

}


/** Companion object for the `AONLimitAskOrder` trait.
  *
  * Provides a constructor for the default implementation of an `AONLimitAskOrder`.
  */
object AONLimitAskOrder {

  /** Creates an instance of a `AONLimitAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `AONLimitAskOrder`.
    * @param limit the minimum price at which the `AONLimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `AONLimitAskOrder` was issued.
    * @param timestamp the time at which the `AONLimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `AONLimitAskOrder` was issued.
    * @param uuid the `UUID` of the `AONLimitAskOrder`.
    * @return an instance of an `AONLimitAskOrder`.
    */
  def apply(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable, uuid: UUID): AONLimitAskOrder = {
    DefaultImpl(issuer, limit, quantity, timestamp, tradable, uuid)
  }


  /** Class providing a default implementation of an `AONLimitAskOrder`.
    *
    * @param issuer the `UUID` of the actor that issued the `AONLimitAskOrder`.
    * @param limit the minimum price at which the `AONLimitAskOrder` can be executed.
    * @param quantity the number of units of the `tradable` for which the `AONLimitAskOrder` was issued.
    * @param timestamp the time at which the `AONLimitAskOrder` was issued.
    * @param tradable the `Tradable` for which the `AONLimitAskOrder` was issued.
    * @param uuid the `UUID` of the `AONLimitAskOrder`.
    * @return an instance of a `AONLimitAskOrder`.
    */
  private[this] case class DefaultImpl(issuer: UUID, limit: Price, quantity: Long, timestamp: Long, tradable: Tradable,
                                       uuid: UUID) 
    extends AONLimitAskOrder

}
