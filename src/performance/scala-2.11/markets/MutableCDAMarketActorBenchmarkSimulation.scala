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
import akka.testkit.TestKit

import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import markets.actors.MutableTreeSetCDAMarketActor
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.actors.participants.strategies.RandomOrderIssuingStrategy
import markets.actors.participants.{Add, IssueAskOrder, IssueBidOrder, TestOrderIssuer}
import markets.actors.settlement.TestSettlementMechanismActor
import markets.orders.{AskOrder, BidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.commons.math3.random.{MersenneTwister, SynchronizedRandomGenerator}

import scala.concurrent.ExecutionContext
import scala.util.Random


object MutableCDAMarketActorBenchmarkSimulation extends App {

  val appConfig = ConfigFactory.load("marketActorBenchmark.conf")
    .withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(args(0)))
    .withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-max", ConfigValueFactory.fromAnyRef(args(0)))
    //.withValue("akka.actor.deployment./brokerage.pool-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(args(0)))
    //.withValue("akka.actor.deployment./brokerage.pool-dispatcher.fork-join-executor.parallelism-max", ConfigValueFactory.fromAnyRef(args(0)))
  
  val testKit = new TestKit(ActorSystem("MutableCDAMarketActorBenchmarkSimulation", appConfig))

  val prng: Random = new Random(42)

  /* Setup the tradables. */
  val numberTradables = appConfig.getInt("simulation.tradables.number")
  val tradables = for (i <- 1 to numberTradables) yield {
    val symbolLength = appConfig.getInt("simulation.tradables.symbol-length")
    val symbol = prng.nextString(symbolLength)
    val tick = appConfig.getInt("simulation.tradables.tick")
    Tradable(symbol, tick)
  }

  /* Setup the tickers. */
  val tickConfig = appConfig.getConfig("simulation.tickers.initial-tick")
  val ticker = initializeTicker(tickConfig)(testKit.system.dispatcher)
  val tickers = tradables.map {
    tradable => tradable -> ticker
  } (collection.breakOut): Map[Tradable, Agent[Tick]]

  /* Setup a SettlementMechanismActor. */
  val settlementMechanism = testKit.system.actorOf(Props[TestSettlementMechanismActor])

  /* Setup the MarketActors. */
  val markets = tradables.map { tradable =>
    val askOrdering = AskPriceTimeOrdering
    val bidOrdering = BidPriceTimeOrdering
    val referencePrice = appConfig.getLong("simulation.tradables.reference-price")
    val ticker = tickers(tradable)
    val props = MutableTreeSetCDAMarketActor.props(askOrdering, bidOrdering,
      referencePrice, settlementMechanism, ticker, tradable)
    tradable -> testKit.system.actorOf(props)
  } (collection.breakOut): Map[Tradable, ActorRef]

  /* Create trading instructions. */
  val numberOrders = appConfig.getInt("simulation.order-instructions.number")
  val askOrderProbability = appConfig.getDouble("simulation.order-instructions.ask-order-probability")
  val instructions = for (i <- 1 to numberOrders) yield {
    if (askOrderProbability < prng.nextDouble()) IssueAskOrder else IssueBidOrder
  }

  /* Setup the BrokerageActor. */
  val rng = new SynchronizedRandomGenerator(new MersenneTwister(42))

  val askPriceDistribution = new UniformRealDistribution(rng, 1.0, 200.0)
  val askQuantityDistribution = new UniformRealDistribution(rng, 1.0, 10.0)
  val askOrderIssuingStrategy = RandomOrderIssuingStrategy[AskOrder](0.5, rng, askPriceDistribution,
    askQuantityDistribution)

  val bidPriceDistribution = new UniformRealDistribution(rng, 1.0, 200.0)
  val bidQuantityDistribution = new UniformRealDistribution(rng, 1.0, 10.0)
  val bidOrderIssuingStrategy = RandomOrderIssuingStrategy[BidOrder](0.5, rng, bidPriceDistribution,
    bidQuantityDistribution)

  val orderIssuerProps = TestOrderIssuer.props(askOrderIssuingStrategy, bidOrderIssuingStrategy)
  val brokerage = testKit.system.actorOf(FromConfig.props(orderIssuerProps), "brokerage")

  /* Setup the MarketRegulatorActor. */
  val participants = Set(brokerage)
  val marketRegulator = testKit.system.actorOf(MarketRegulatorActor.props(participants, markets.values))

  /* Run the simulation. */
  tradables.foreach { tradable =>
    val market = markets(tradable)
    val ticker = tickers(tradable)
    brokerage ! Broadcast(Add(tradable, market, ticker))
  }
  instructions.foreach(instruction => brokerage ! instruction)
  brokerage ! Broadcast(PoisonPill)

  def initializeTicker(config: Config)(implicit ec: ExecutionContext): Agent[Tick] = {
    val askPrice = config.getLong("ask-price")
    val bidPrice = config.getLong("bid-price")
    val price = config.getLong ("price")
    val quantity = config.getLong("quantity")
    val timestamp = System.currentTimeMillis()
    val initialTick = Tick(askPrice, bidPrice, price, quantity, timestamp)
    Agent(initialTick)(ec)
  }
}
