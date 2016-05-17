package markets.actors.participants.strategies

import markets.actors.participants.strategies.investment.{ConstantInvestmentStrategy, InvestmentStrategy}
import markets.actors.participants.strategies.trading.{ConstantTradingStrategy, TradingStrategy}
import markets.orders.Order
import markets.tradables.Tradable

import scala.reflect.internal.util.Statistics.Quantity


/** Class implementing an
  * [[markets.actors.participants.strategies.OrderIssuingStrategy `OrderIssuingStrategy`]] that
  * submits various [[markets.orders.Order `Order`]] types with a constant `price`, `quantity`,
  * and `tradable`.
  *
  * @param price
  * @param quantity
  * @param tradable
  * @tparam T Type of `Order` issued. Should be either `AskOrder` or `BidOrder`.
  * @note This implementation should be used primarily for testing purposes.
  */
class ConstantOrderIssuingStrategy[T <: Order](val price: Option[Long],
                                               val quantity: Long,
                                               val tradable: Option[Tradable])
  extends OrderIssuingStrategy[T] {

  val investmentStrategy: InvestmentStrategy[T] = ConstantInvestmentStrategy[T](tradable)

  val tradingStrategy: TradingStrategy[T] = ConstantTradingStrategy[T](price, quantity)

}


object ConstantOrderIssuingStrategy {

  def apply[T <: Order](price: Option[Long],
                        quantity: Long,
                        tradable: Option[Tradable]): OrderIssuingStrategy[T] = {
    new ConstantOrderIssuingStrategy[T](price, quantity, tradable)
  }

}