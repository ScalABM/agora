package markets.actors.participants.strategies

import markets.actors.participants.strategies.investment.TestInvestmentStrategy
import markets.actors.participants.strategies.trading.TestTradingStrategy
import markets.orders.Order
import markets.tradables.Tradable


/** Stub implementation of an `OrderIssuingStrategy` for testing purposes.
  *
  * @param price the desired price for the `Order`
  * @param quantity the desired quantity for the `Order`
  * @param tradable the desired tradable for the `Order`
  * @tparam T either `AskOrder` or `BidOrder`, depending on whether `OrderIssuingStrategy` is
  *           used to issue `AskOrder` or `BidOrder`.
  * @note If `price` is `Some(limitPrice)`, then `LimitOrder` will be issued; otherwise if `price`
  *       is `None`, then `MarketOrder` will be issued. If `quantity` is `0`, then `tradingStrategy`
  *       returns `None`: setting `quantity=0` is used to mimic the behavior of an infeasible
  *       trading strategy.
  */
class TestOrderIssuingStrategy[T <: Order](val price: Option[Long],
                                           val quantity: Long,
                                           val tradable: Tradable)
  extends OrderIssuingStrategy[T] {

  val investmentStrategy = TestInvestmentStrategy[T](tradable)

  val tradingStrategy = TestTradingStrategy[T](price, quantity)

}


object TestOrderIssuingStrategy {

  def apply[T <: Order](price: Option[Long],
                        quantity: Long,
                        tradable: Tradable): OrderIssuingStrategy[T] = {
    new TestOrderIssuingStrategy[T](price, quantity, tradable)
  }

}