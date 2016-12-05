package org.economicsl.agora.markets

import org.economicsl.agora.markets.tradables.orders.ask.AskOrder
import org.economicsl.agora.markets.tradables.orders.bid.BidOrder
import org.economicsl.agora.markets.tradables.{LimitPrice, Price, Quantity}


/** Used to store information associated with a transaction between a buyer and a seller.
  *
  * @param askOrder an instance of `LimitAskOrder with Quantity`.
  * @param bidOrder an instance of `LimitBidOrder with Quantity`.
  * @param price the price at which the transaction between the `buyer` and `seller` will be settled.
  * @param quantity the quantity of the `Tradable` that will be exchanged during settlement.
  * @note a `Fill` needs to contain all relevant information required to settle the transaction.
  */
class Fill(val askOrder: AskOrder with LimitPrice with Quantity, val bidOrder: BidOrder with LimitPrice with Quantity, val price: Price, val quantity: Long)

