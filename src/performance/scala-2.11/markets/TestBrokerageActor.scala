package markets

import akka.actor.{Actor, PoisonPill, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

import markets.actors.participants.issuers.TestOrderIssuer
import markets.strategies.OrderIssuingStrategy
import markets.orders.{AskOrder, BidOrder, Order}


class TestBrokerageActor(numberOrderIssuers: Int) extends Actor {

  val orderIssuers = for (i <- 1 to numberOrderIssuers) yield {
    val askOrderIssuingStrategy = orderIssuingStrategyFactory[AskOrder]()
    val bidOrderIssuingStrategy = orderIssuingStrategyFactory[BidOrder]()
    context.actorOf(TestOrderIssuer.props(askOrderIssuingStrategy, bidOrderIssuingStrategy))
  }
  orderIssuers.foreach(orderIssuer => context watch orderIssuer)

  var router = Router(RoundRobinRoutingLogic(), orderIssuers.map(orderIssuer => ActorRefRoutee(orderIssuer)))

  def orderIssuingStrategyFactory[T <: Order](): OrderIssuingStrategy[T]

  def receive = {
    case Terminated(a) =>
      router = router.removeRoutee(a)
      if (router.routees.isEmpty) {
        self ! PoisonPill
      }
    case message =>
      router.route(message, sender())
  }
}


object TestBrokerageActor {

  def props(askOrderIssuingStrategy: OrderIssuingStrategy[AskOrder],
            bidOrderIssuingStrategy: OrderIssuingStrategy[BidOrder],
            numberOrderIssuers: Int): Props = {
    Props(new TestBrokerageActor(askOrderIssuingStrategy, bidOrderIssuingStrategy, numberOrderIssuers))
  }
}