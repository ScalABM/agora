package markets.actors.participants.issuers
import akka.actor.{ActorRef, Props}
import akka.agent.Agent

import markets.orders.{AskOrder, BidOrder}
import markets.strategies.TestRandomOrderIssuingStrategy
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.random.RandomGenerator


class TestRandomOrderIssuer(prng: RandomGenerator,
                            config: TestRandomOrderIssuerConfig) extends OrderIssuer {

  val askOrderIssuingStrategy = {
    TestRandomOrderIssuingStrategy[AskOrder](prng, config.askOrderIssuingStrategyConfig)
  }

  val bidOrderIssuingStrategy = {
    TestRandomOrderIssuingStrategy[BidOrder](prng, config.bidOrderIssuingStrategyConfig)
  }

  var tickers = Map.empty[Tradable, Agent[Tick]]

  var markets = Map.empty[Tradable, ActorRef]

}


object TestRandomOrderIssuer {

  def apply(prng: RandomGenerator,
            config: TestRandomOrderIssuerConfig): TestRandomOrderIssuer = {
    new TestRandomOrderIssuer(prng, config)
  }

  def props(prng: RandomGenerator,
            config: TestRandomOrderIssuerConfig): Props = {
    Props(TestRandomOrderIssuer(prng, config))
  }

}