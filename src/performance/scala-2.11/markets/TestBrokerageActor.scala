package markets

import akka.actor.{ActorRef, PoisonPill, Props, Terminated}
import akka.agent.Agent
import akka.routing.{ActorRefRoutee, Broadcast, Router, RandomRoutingLogic}

import markets.participants.{SubmitOrder, TestOrderIssuer}
import markets.participants.strategies.TradingStrategy
import markets.tickers.Tick
import markets.tradables.Tradable

import scala.collection.{immutable, mutable}


/** Class representing a collection of TraderActors sharing a common trading strategy.
  *
  * @param numberBrokers
  * @param markets
  * @param tickers
  * @param tradingStrategy
  */
class TestBrokerageActor(instructions: immutable.IndexedSeq[SubmitOrder],
                         numberBrokers: Int,
                         markets: mutable.Map[Tradable, ActorRef],
                         tickers: mutable.Map[Tradable, Agent[Tick]],
                         tradingStrategy: TradingStrategy)
  extends StackableActor {

  wrappedBecome(brokerageBehavior)

  val brokers = for (i <- 1 to numberBrokers) yield {
    val brokerProps = TestOrderIssuer.props(markets, tickers, tradingStrategy)
    val broker = context.actorOf(brokerProps)
    ActorRefRoutee(broker)
  }

  /** Brokerage randomly distributed instructions across its available brokers. */
  var router = Router(RandomRoutingLogic(), brokers)

  /* Watch all brokers so that Brokerage is notified when any broker terminates. */
  brokers.foreach(broker => context watch broker.ref)

  instructions.foreach(instruction => router.route(instruction, self))
  router.route(Broadcast(PoisonPill), self)

  //override def postStop(): Unit = {
  //  markets.foreach{ case (_, market) => market tell(PoisonPill, self) }
  //}

  def brokerageBehavior: Receive = {
    case Terminated(broker) =>
      router = router.removeRoutee(broker)
      if (router.routees.isEmpty) {
        /*markets.foreach {
          case (_, market) => market tell(PoisonPill, self)
        }*/
        self tell(PoisonPill, self)
      }
  }

}


object TestBrokerageActor {

  def props(instructions: immutable.IndexedSeq[SubmitOrder],
            numberBrokers: Int,
            markets: mutable.Map[Tradable, ActorRef],
            tickers: mutable.Map[Tradable, Agent[Tick]],
            tradingStrategy: TradingStrategy): Props = {
    Props(new TestBrokerageActor(instructions, numberBrokers, markets, tickers, tradingStrategy))
  }

}