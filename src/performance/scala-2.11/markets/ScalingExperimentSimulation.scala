/*
Copyright 2016 David R. Pugh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package markets

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.agent.Agent
import akka.routing.{Broadcast, FromConfig}

import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import markets.actors.MutableTreeSetCDAMarketActor
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.actors.participants.{Add, GodeSunderZIOrderIssuer, GodeSunderZIOrderIssuerConfig, IssueAskOrder, IssueBidOrder}
import markets.actors.settlement.TestSettlementMechanismActor
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random


object ScalingExperimentSimulation extends App {

  val config = ConfigFactory.load("scalingExperiment.conf")
    .withValue("akka.actor.deployment./brokerage.nr-of-instances", ConfigValueFactory.fromAnyRef(args(0)))

  val system = ActorSystem("ScalingExperimentSimulation", config)

  val prng: Random = new Random(42)

  /* Setup the tradables. */
  val numberTradables = config.getInt("simulation.tradables.number")
  val tradables = for (i <- 1 to numberTradables) yield {
    val symbolLength = config.getInt("simulation.tradables.symbol-length")
    val symbol = prng.nextString(symbolLength)
    val tick = config.getInt("simulation.tradables.tick")
    Tradable(symbol, tick)
  }

  /* Setup the tickers. */
  val tickConfig = config.getConfig("simulation.tickers.initial-tick")
  val initialTick = Tick(1, 1, 1, 1, 1)
  val ticker = Agent(initialTick)(system.dispatcher)
  val tickers = tradables.map {
    tradable => tradable -> ticker
  } (collection.breakOut): Map[Tradable, Agent[Tick]]

  /* Setup a SettlementMechanismActor. */
  val settlementMechanism = system.actorOf(Props[TestSettlementMechanismActor])

  /* Setup the MarketActors. */
  val markets = tradables.map { tradable =>
    val askOrdering = AskPriceTimeOrdering
    val bidOrdering = BidPriceTimeOrdering
    val referencePrice = config.getLong("simulation.tradables.reference-price")
    val ticker = tickers(tradable)
    val props = MutableTreeSetCDAMarketActor.props(askOrdering, bidOrdering,
      referencePrice, settlementMechanism, ticker, tradable)
    tradable -> system.actorOf(props)
  } (collection.breakOut): Map[Tradable, ActorRef]

  /* Setup the BrokerageActor. */
  val orderIssuerConfig = GodeSunderZIOrderIssuerConfig(config.getConfig("order-issuers"))
  val orderIssuerProps = GodeSunderZIOrderIssuer.props(orderIssuerConfig)
  val brokerage = system.actorOf(FromConfig.props(orderIssuerProps), "brokerage")

  /* Setup the MarketRegulatorActor. */
  val participants = Set(brokerage)
  val settlementMechanisms = Set(settlementMechanism)
  val marketRegulator = system.actorOf(MarketRegulatorActor.props(participants, markets.values, settlementMechanisms))

  /* Run the simulation. */
  tradables.foreach { tradable =>
    brokerage ! Broadcast(Add(tradable, markets(tradable), tickers(tradable)))
  }

  val numberOrders = config.getInt("simulation.order-instructions.number")
  val askOrderProbability = config.getDouble("simulation.order-instructions.ask-order-probability")
  val instructions = for (i <- 1 to numberOrders) {
    if (prng.nextDouble() <= askOrderProbability) {
      brokerage ! IssueAskOrder
    } else {
      brokerage ! IssueBidOrder
    }
  }

  brokerage ! Broadcast(PoisonPill)

  // Need to block the main thread until ActorSystem terminates!
  Await.result(system.whenTerminated, Duration.Inf)

}
