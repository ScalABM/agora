package markets.participants

import markets.tradables.Tradable


trait LimitOrderTradingStrategy {

  def execute(): (Long, Long, Tradable)

}
