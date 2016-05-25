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

import akka.actor.{ActorSystem, PoisonPill}
import akka.agent.Agent
import akka.routing.{ActorRefRoutee, Broadcast, RoundRobinRoutingLogic}

import com.typesafe.config.ConfigFactory
import markets.actors.MutableTreeSetCDAMarketActor
import markets.actors.participants.issuers.TestRandomOrderIssuer
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.actors.participants.{Add, IssueAskOrder, IssueBidOrder}
import markets.tickers.Tick
import markets.tradables.Tradable
import org.apache.commons.math3.random.MersenneTwister

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object AkkaReferenceConfigBenchmarkSimulation extends App {

  val config = ConfigFactory.load("scalingExperiment.conf")

  val system = ActorSystem("AkkaReferenceConfigBenchmarkSimulation", config)

  val benchmarkSimulationConfig = BenchmarkSimulationConfig(config)

  val prng = new MersenneTwister(benchmarkSimulationConfig.seed)

  /* Setup the markets. */
  val numberTradables = args(0).toInt
  val tradables = for (i <- 1 to numberTradables) yield {
    val tradable = Tradable(symbol=i.toString, tick=1)
    val ticker = Agent(Tick(1, 1, 1, 1, 1))(system.dispatcher)
    val askOrdering = AskPriceTimeOrdering
    val bidOrdering = BidPriceTimeOrdering
    val referencePrice = 1
    val settlementMechanism = system.deadLetters  // don't care about fills when benchmarking!
    val marketProps = MutableTreeSetCDAMarketActor.props(askOrdering, bidOrdering,
      referencePrice, settlementMechanism, ticker, tradable)
    val market = system.actorOf(marketProps)

    tradable -> (market, ticker)
  }

  /* Setup the order issuers. */
  val numberOrderIssuers = args(1).toInt
  val orderIssuers = for (i <- 1 to numberOrderIssuers) yield {
    val rng = new MersenneTwister(prng.nextInt())
    val orderIssuerConfig = benchmarkSimulationConfig.orderIssuerConfig
    val orderIssuerProps = TestRandomOrderIssuer.props(rng, orderIssuerConfig)
    system.actorOf(orderIssuerProps)
  }

  /* Create a router that uses order issuers as routees. */
  val routees = orderIssuers.map(orderIssuer => ActorRefRoutee(orderIssuer))
  val routingLogic = RoundRobinRoutingLogic()
  val brokerage = system.actorOf(TestBrokerageActor.props(routingLogic, routees))

  /* Setup the MarketRegulatorActor. */
  val markets = tradables.map { case (_, (market, _)) => market }
  val marketRegulator = system.actorOf(MarketRegulatorActor.props(orderIssuers, markets))

  /* Run the simulation. */
  tradables.foreach {
    case (tradable, (market, ticker)) => brokerage ! Broadcast(Add(tradable, market, ticker))
  }

  val numberOrders = args(2).toInt
  val askOrderProbability = args(3).toDouble
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
