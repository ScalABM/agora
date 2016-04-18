package markets

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.agent.Agent
import akka.testkit.TestKit

import com.typesafe.config.{Config, ConfigFactory}
import markets.engines.CDAMatchingEngine
import markets.orders.orderings.ask.AskPriceTimeOrdering
import markets.orders.orderings.bid.BidPriceTimeOrdering
import markets.participants.strategies.TestRandomTradingStrategy
import markets.participants.{SubmitAskOrder, SubmitBidOrder}
import markets.settlement.TestSettlementMechanismActor
import markets.tickers.Tick
import markets.tradables.{TestTradable, Tradable}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.Random


object MarketActorBenchmarkSimulation extends App {

  val baseConfig = ConfigFactory.load("marketActorBenchmark.conf")
  val akkaConfig = ConfigFactory.parseString(s"akka.actor.default-dispatcher.fork-join-executor.parallelism-max=${args(0)}")
  val appConfig = akkaConfig.withFallback(baseConfig)

  val testKit = new TestKit(ActorSystem("MarketActorBenchmark", appConfig))

  val prng = new Random(appConfig.getLong("market-actor-benchmark.seed"))

  /* Setup the tradables. */
  val numberTradables = appConfig.getInt("market-actor-benchmark.tradables.number")
  val tradables = for (i <- 1 to numberTradables) yield {
    val symbolLength = appConfig.getInt("market-actor-benchmark.tradables.symbol-length")
    val symbol = prng.nextString(symbolLength)
    TestTradable(symbol)
  }

  /* Setup the tickers. */
  val initialTick = Tick(1, 1, 1, 1, 1)  // todo figure out how to initialize object from config!
  val ticker = Agent(initialTick)(testKit.system.dispatcher)
  val tickers = tradables.map {
    tradable => tradable -> Agent(initialTick)(testKit.system.dispatcher)
  } (collection.breakOut): mutable.Map[Tradable, Agent[Tick]]

  /* Setup a SettlementMechanismActor. */
  val settlementMechanism = testKit.system.actorOf(Props[TestSettlementMechanismActor])

  /* Setup the MarketActors. */
  val markets = tradables.map { tradable =>
    val referencePrice = appConfig.getLong("market-actor-benchmark.tradables.reference-price")
    val matchingEngine = CDAMatchingEngine(AskPriceTimeOrdering, BidPriceTimeOrdering, referencePrice)
    val ticker = tickers(tradable)
    val props = MarketActor.props(matchingEngine, settlementMechanism, ticker, tradable)
    tradable -> testKit.system.actorOf(props)
  } (collection.breakOut): mutable.Map[Tradable, ActorRef]

  /* Create trading instructions. */
  val numberOrders = appConfig.getInt("market-actor-benchmark.orders.number")
  val askOrderProbability = appConfig.getDouble("market-actor-benchmark.orders.askOrderProbability")
  val instructions = for (i <- 1 to numberOrders) yield {
    if (askOrderProbability < prng.nextDouble()) SubmitAskOrder else SubmitBidOrder
  }

  /* Setup the BrokerageActor. */
  val numberBrokers = appConfig.getInt("market-actor-benchmark.brokerage.number-brokers")
  val tradingStrategy = new TestRandomTradingStrategy(prng)
  val brokerageProps = TestBrokerageActor.props(instructions, numberBrokers, markets, tickers, tradingStrategy)
  val brokerage = testKit.system.actorOf(brokerageProps)

  /* Setup the MarketRegulator. */
  val marketRegulator = testKit.system.actorOf(MarketRegulator.props(brokerage, markets))

  def initializeTicker(config: Config)(implicit ec: ExecutionContext): Agent[Tick] = {
    val initialTick = Tick(1, 1, 1, 1, 1)
    Agent(initialTick)(ec)
  }
}
