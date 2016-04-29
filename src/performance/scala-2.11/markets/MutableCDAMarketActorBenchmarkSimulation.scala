package markets

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.agent.Agent
import akka.routing.{Broadcast, FromConfig}
import akka.testkit.TestKit

import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import markets.actors.MutableTreeSetCDAMarketActor
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.actors.participants.strategies.{RandomTradingStrategyConfig, TestRandomTradingStrategy}
import markets.actors.participants.{SubmitAskOrder, SubmitBidOrder, TestOrderIssuer}
import markets.actors.settlement.TestSettlementMechanismActor
import markets.tickers.Tick
import markets.tradables.{TestTradable, Tradable}

import scala.concurrent.ExecutionContext
import scala.util.Random


object MutableCDAMarketActorBenchmarkSimulation extends App {

  val appConfig = ConfigFactory.load("marketActorBenchmark.conf")
    .withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(args(0)))
    .withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-max", ConfigValueFactory.fromAnyRef(args(0)))
    //.withValue("akka.actor.deployment./brokerage.pool-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(args(0)))
    //.withValue("akka.actor.deployment./brokerage.pool-dispatcher.fork-join-executor.parallelism-max", ConfigValueFactory.fromAnyRef(args(0)))
  
  val testKit = new TestKit(ActorSystem("MutableCDAMarketActorBenchmarkSimulation", appConfig))

  val prng = new Random(appConfig.getLong("simulation.seed"))

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
    if (askOrderProbability < prng.nextDouble()) SubmitAskOrder else SubmitBidOrder
  }

  /* Setup the BrokerageActor. */
  val strategyConfig = RandomTradingStrategyConfig(appConfig.getConfig("simulation.order-issuers.trading-strategy"))
  val tradingStrategy = TestRandomTradingStrategy(strategyConfig, prng)
  val orderIssuerProps = TestOrderIssuer.props(markets, tickers, tradingStrategy)
  val brokerage = testKit.system.actorOf(FromConfig.props(orderIssuerProps), "brokerage")

  /* Setup the MarketRegulatorActor. */
  val participants = Set(brokerage)
  val marketRegulator = testKit.system.actorOf(MarketRegulatorActor.props(participants, markets.values))

  /* Run the simulation. */
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
