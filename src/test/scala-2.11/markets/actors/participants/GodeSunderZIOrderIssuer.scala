package markets.actors.participants
import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.actors.participants.strategies.{GodeSunderZIOrderIssuingStrategy, GodeSunderZIOrderIssuingStrategyConfig}
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.random.MersenneTwister


class GodeSunderZIOrderIssuer(config: GodeSunderZIOrderIssuerConfig) extends OrderIssuer {

  val prng = {
    config.seed match {
      case Some(long) =>
        new MersenneTwister(long)
      case None =>
        new MersenneTwister()
    }
  }

  val askOrderIssuingStrategyConfig = {
    GodeSunderZIOrderIssuingStrategyConfig(config.askOrderIssuingStrategyConfig)
  }

  val askOrderIssuingStrategy = {
    GodeSunderZIOrderIssuingStrategy[AskOrder](askOrderIssuingStrategyConfig, prng)
  }

  val bidOrderIssuingStrategyConfig = {
    GodeSunderZIOrderIssuingStrategyConfig(config.bidOrderIssuingStrategyConfig)
  }

  val bidOrderIssuingStrategy = {
    GodeSunderZIOrderIssuingStrategy[BidOrder](bidOrderIssuingStrategyConfig, prng)
  }

  var tickers = Map.empty[Tradable, Agent[Tick]]

  var markets = Map.empty[Tradable, ActorRef]

}


object GodeSunderZIOrderIssuer {

  def props(config: GodeSunderZIOrderIssuerConfig): Props = {
    Props(new GodeSunderZIOrderIssuer(config))
  }

}