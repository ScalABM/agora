package markets.participants

import markets.tradables.Tradable


trait MarketOrderTradingStrategy {

  def execute(): (Long, Tradable)

}