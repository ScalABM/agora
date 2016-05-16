package markets.actors.participants.strategies.trading

import akka.agent.Agent

import markets.tickers.Tick
import markets.tradables.Tradable

/** Base trait for all trading strategies. */
trait TradingStrategy {

  /** Rule used to specify a price for an [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def getAskPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long]

  /** Rule used to specify a quantity for an [[markets.orders.AskOrder `AskOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def getAskQuantity(ticker: Agent[Tick], tradable: Tradable): Long

  /** Rule used to specify a price for a [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return either `Some(price)` or `None` depending.
    */
  def getBidPrice(ticker: Agent[Tick], tradable: Tradable): Option[Long]

  /** Rule used to specify a quantity for a [[markets.orders.BidOrder `BidOrder`]].
    *
    * @param ticker an [[akka.agent.Agent `Agent`]] storing the current market price of the
    *               `tradable`.
    * @param tradable some [[markets.tradables.Tradable `Tradable`]] object.
    * @return the desired quantity.
    */
  def getBidQuantity(ticker: Agent[Tick], tradable: Tradable): Long

}
