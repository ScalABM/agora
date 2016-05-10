package markets.actors.participants.strategies


/** Base trait for all trading strategies that specify fixed prices and quantities. */
trait FixedTradingStrategy extends TradingStrategy with FixedPrices with FixedQuantities
